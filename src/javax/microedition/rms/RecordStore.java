package javax.microedition.rms;

import java.util.Hashtable;
import java.util.Vector;

/* In-memory RecordStore (save data survives within a run, not across reboots — persistence
 * to flash is a later increment). Records are 1-indexed; a deleted record's slot holds null. */
public class RecordStore {
    private static final Hashtable<String, RecordStore> STORES = new Hashtable<>();

    private final String name;
    private final Vector<byte[]> records = new Vector<>();  // index = id-1
    private long lastModified = System.currentTimeMillis();
    private boolean open = true;

    private RecordStore(String name) { this.name = name; }

    public static RecordStore openRecordStore(String name, boolean createIfNecessary)
            throws RecordStoreException {
        RecordStore rs = STORES.get(name);
        if (rs == null) {
            if (!createIfNecessary) throw new RecordStoreException("not found: " + name);
            rs = new RecordStore(name);
            STORES.put(name, rs);
        }
        rs.open = true;
        return rs;
    }

    public static void deleteRecordStore(String name) throws RecordStoreException {
        if (STORES.remove(name) == null) throw new RecordStoreException("not found: " + name);
    }

    public void closeRecordStore() throws RecordStoreException { open = false; }

    public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreException {
        ensureOpen();
        byte[] rec = new byte[numBytes];
        if (data != null) System.arraycopy(data, offset, rec, 0, numBytes);
        records.addElement(rec);
        lastModified = System.currentTimeMillis();
        return records.size();                 // id = 1-based
    }

    public byte[] getRecord(int recordId) throws RecordStoreException {
        byte[] rec = slot(recordId);
        byte[] copy = new byte[rec.length];
        System.arraycopy(rec, 0, copy, 0, rec.length);
        return copy;
    }

    public void setRecord(int recordId, byte[] newData, int offset, int numBytes)
            throws RecordStoreException {
        ensureOpen();
        slot(recordId);                        // validates id
        byte[] rec = new byte[numBytes];
        if (newData != null) System.arraycopy(newData, offset, rec, 0, numBytes);
        records.setElementAt(rec, recordId - 1);
        lastModified = System.currentTimeMillis();
    }

    public void deleteRecord(int recordId) throws RecordStoreException {
        slot(recordId);
        records.setElementAt(null, recordId - 1);
        lastModified = System.currentTimeMillis();
    }

    public int getRecordSize(int recordId) throws RecordStoreException { return slot(recordId).length; }

    public int getNumRecords() throws RecordStoreException {
        ensureOpen();
        int n = 0;
        for (int i = 0; i < records.size(); i++) if (records.elementAt(i) != null) n++;
        return n;
    }

    public int getNextRecordID() throws RecordStoreException { ensureOpen(); return records.size() + 1; }
    public long getLastModified() throws RecordStoreException { ensureOpen(); return lastModified; }
    public int getSize() { return 0; }
    public int getSizeAvailable() { return 1 << 20; }
    public String getName() { return name; }
    public int getVersion() { return 1; }

    private void ensureOpen() throws RecordStoreNotOpenException {
        if (!open) throw new RecordStoreNotOpenException(name);
    }
    private byte[] slot(int recordId) throws RecordStoreException {
        ensureOpen();
        if (recordId < 1 || recordId > records.size()) throw new InvalidRecordIDException("id=" + recordId);
        byte[] rec = records.elementAt(recordId - 1);
        if (rec == null) throw new InvalidRecordIDException("deleted id=" + recordId);
        return rec;
    }
}
