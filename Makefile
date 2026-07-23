PROFILE             :=  midp
SHELL               :=  /usr/bin/bash
JDK_BIN             ?=  C:/Program Files/Eclipse Adoptium/jdk-25.0.1.8-hotspot/bin
JC                  :=  $(JDK_BIN)/javac.exe
JAR                 :=  $(JDK_BIN)/jar.exe
SOURCE_PATH         :=  src
API_LIST            :=  config/jsr118-api-classes.txt
PACKAGER_SOURCE     :=  tools/MidpJarPackager.java
OUT_DIR             ?=  bin
FLINTOS             ?=  ../FlintOSOrigin/FlintOS
JDK8_RT             ?=  C:/Program Files/Eclipse Adoptium/jdk-8.0.492.9-hotspot/jre/lib/rt.jar

RUN_OPT             :=
DEV_OPT             :=  -g
LIB_DIR             :=  $(FLINTOS)/files/lib
JAVA_LIB_DIR        :=  $(shell /usr/bin/cygpath -m "$(LIB_DIR)")
JAVA_JDK8_RT        :=  $(shell /usr/bin/cygpath -m "$(JDK8_RT)")
JFLAGS              :=  -source 8 -target 8 -XDstringConcat=inline -bootclasspath "$(JAVA_JDK8_RT)" -Xlint:all,-serial,-options -encoding UTF-8
CLASSPATH           :=  $(JAVA_LIB_DIR)/flint.drawing.jar;$(JAVA_LIB_DIR)/flintos.device.jar;$(JAVA_LIB_DIR)/midp.jar
SOURCES             :=  $(shell /usr/bin/find $(SOURCE_PATH) -name '*.java' | /usr/bin/sort)

RUN_DIR             :=  $(OUT_DIR)/run
DEV_DIR             :=  $(OUT_DIR)/dev
TOOLS_DIR           :=  $(OUT_DIR)/tools

all: run dev

run: $(RUN_DIR)/$(PROFILE)

dev: $(DEV_DIR)/$(PROFILE)

$(RUN_DIR)/$(PROFILE): FORCE
	@echo Compiling $(PROFILE) for runtime mode
	@rm -rf $(RUN_DIR)/$(PROFILE)
	@mkdir -p $(RUN_DIR)/$(PROFILE)
	@"$(JC)" $(RUN_OPT) $(JFLAGS) -classpath "$(CLASSPATH)" -d $(RUN_DIR)/$(PROFILE) $(SOURCES)
	@mkdir -p $(TOOLS_DIR)
	@"$(JC)" --release 17 -encoding UTF-8 -d $(TOOLS_DIR) $(PACKAGER_SOURCE)
	@"$(JDK_BIN)/java.exe" -cp $(TOOLS_DIR) MidpJarPackager $(RUN_DIR)/$(PROFILE) $(SOURCE_PATH) META-INF/MANIFEST.MF $(API_LIST) $(RUN_DIR) run

$(DEV_DIR)/$(PROFILE): FORCE
	@echo Compiling $(PROFILE) for development mode
	@rm -rf $(DEV_DIR)/$(PROFILE)
	@mkdir -p $(DEV_DIR)/$(PROFILE)
	@"$(JC)" $(DEV_OPT) $(JFLAGS) -classpath "$(CLASSPATH)" -d $(DEV_DIR)/$(PROFILE) $(SOURCES)
	@mkdir -p $(TOOLS_DIR)
	@"$(JC)" --release 17 -encoding UTF-8 -d $(TOOLS_DIR) $(PACKAGER_SOURCE)
	@"$(JDK_BIN)/java.exe" -cp $(TOOLS_DIR) MidpJarPackager $(DEV_DIR)/$(PROFILE) $(SOURCE_PATH) META-INF/MANIFEST.MF $(API_LIST) $(DEV_DIR) dev

clean:
	@rm -rf $(OUT_DIR)
	@echo Clean complete

FORCE:
