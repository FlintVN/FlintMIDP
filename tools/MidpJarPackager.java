import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

public final class MidpJarPackager {
    private enum Artifact { MIDP, RUNTIME, M3G }

    private static final class Entry {
        final Path file;
        final String name;

        Entry(Path file, String name) {
            this.file = file;
            this.name = name;
        }
    }

    private MidpJarPackager() {
    }

    public static void main(String[] args) throws Exception {
        if(args.length != 6) {
            throw new IllegalArgumentException(
                    "classes source manifest api-list output mode(run|dev)");
        }

        Path classes = Paths.get(args[0]).toAbsolutePath().normalize();
        Path sources = Paths.get(args[1]).toAbsolutePath().normalize();
        Path manifestFile = Paths.get(args[2]).toAbsolutePath().normalize();
        Path apiList = Paths.get(args[3]).toAbsolutePath().normalize();
        Path output = Paths.get(args[4]).toAbsolutePath().normalize();
        boolean development = "dev".equals(args[5]);
        if(!development && !"run".equals(args[5]))
            throw new IllegalArgumentException("mode must be run or dev");

        Set<String> apiClasses = readApiClasses(apiList);
        List<Entry> midp = new ArrayList<Entry>();
        List<Entry> runtime = new ArrayList<Entry>();
        List<Entry> m3g = new ArrayList<Entry>();

        collectClasses(classes, apiClasses, midp, runtime, m3g);
        if(development)
            collectSources(sources, apiClasses, midp, runtime, m3g);

        Files.createDirectories(output);
        Manifest base;
        try(InputStream input = Files.newInputStream(manifestFile)) {
            base = new Manifest(input);
        }
        writeJar(output.resolve("j2me.jar"), manifestFor(base, Artifact.MIDP), midp, !development);
        writeJar(output.resolve("flintos.midp.jar"), manifestFor(base, Artifact.RUNTIME), runtime, !development);
        writeJar(output.resolve("m3g.jar"), manifestFor(base, Artifact.M3G), m3g, !development);
    }

    private static Set<String> readApiClasses(Path file) throws IOException {
        Set<String> classes = new HashSet<String>();
        for(String line : Files.readAllLines(file)) {
            String value = line.trim();
            if(!value.isEmpty() && !value.startsWith("#"))
                classes.add(value);
        }
        return classes;
    }

    private static void collectClasses(Path root, Set<String> api,
            List<Entry> midp, List<Entry> runtime, List<Entry> m3g) throws IOException {
        try(java.util.stream.Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".class"))
                    .sorted().forEach(path -> {
                        String name = relative(root, path);
                        String topLevel = name.substring(0, name.length() - 6);
                        int nested = topLevel.indexOf('$');
                        if(nested >= 0) topLevel = topLevel.substring(0, nested);
                        target(topLevel, api, midp, runtime, m3g).add(new Entry(path, name));
                    });
        }
    }

    private static void collectSources(Path root, Set<String> api,
            List<Entry> midp, List<Entry> runtime, List<Entry> m3g) throws IOException {
        try(java.util.stream.Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java"))
                    .sorted().forEach(path -> {
                        String relative = relative(root, path);
                        String className = relative.substring(0, relative.length() - 5);
                        target(className, api, midp, runtime, m3g)
                                .add(new Entry(path, "src/" + relative));
                    });
        }
    }

    private static List<Entry> target(String className, Set<String> api,
            List<Entry> midp, List<Entry> runtime, List<Entry> m3g) {
        if(className.startsWith("javax/microedition/m3g/")) return m3g;
        return api.contains(className) ? midp : runtime;
    }

    private static String relative(Path root, Path file) {
        return root.relativize(file).toString().replace('\\', '/');
    }

    private static Manifest manifestFor(Manifest source, Artifact artifact) {
        Manifest result = new Manifest(source);
        Attributes values = result.getMainAttributes();
        if(values.getValue(Attributes.Name.MANIFEST_VERSION) == null)
            values.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if(artifact == Artifact.RUNTIME) {
            values.putValue("Specification-Title", "FlintOS MIDP Runtime");
            values.putValue("Specification-Version", "1.0");
            values.putValue("Specification-Vendor", "FlintVN");
            values.putValue("Flint-API-Profile", "FlintOS-MIDP-Runtime");
            values.remove(new Attributes.Name("Flint-JSR"));
            values.putValue("Implementation-Title", "FlintOS MIDP Runtime");
        }
        else if(artifact == Artifact.M3G) {
            values.putValue("Specification-Title", "Mobile 3D Graphics API");
            values.putValue("Specification-Version", "1.0");
            values.putValue("Implementation-Title", "FlintM3G");
            values.putValue("Flint-API-Profile", "M3G-1.0");
            values.putValue("Flint-JSR", "184");
        }
        return result;
    }

    private static void writeJar(Path file, Manifest manifest, List<Entry> entries,
            boolean stored) throws IOException {
        try(OutputStream raw = Files.newOutputStream(file);
                JarOutputStream jar = new JarOutputStream(raw, manifest)) {
            Set<String> names = new HashSet<String>();
            for(Entry item : entries) {
                if(!names.add(item.name))
                    throw new IOException("Duplicate JAR entry: " + item.name);
                byte[] data = Files.readAllBytes(item.file);
                JarEntry entry = new JarEntry(item.name);
                entry.setTime(0L);
                if(stored) {
                    CRC32 crc = new CRC32();
                    crc.update(data);
                    entry.setMethod(ZipEntry.STORED);
                    entry.setSize(data.length);
                    entry.setCompressedSize(data.length);
                    entry.setCrc(crc.getValue());
                }
                jar.putNextEntry(entry);
                jar.write(data);
                jar.closeEntry();
            }
        }
        System.out.println(file + " (" + entries.size() + " entries)");
    }
}
