
# FlintJ2ME — MIDP 2.0 standard API + M3G 3D
# Requires: JDK 17+ with module support
#
# Output:
#   bin/j2me.jar  — Standard MIDP 2.0 API (no FlintOS dependencies)
#   bin/m3g.jar   — JSR-184 M3G 3D
#
# FlintOS-specific MIDP classes (Display, Canvas, MIDlet, etc.) are in
# flintos.midp.jar. This JAR contains only the truly portable MIDP files.

JC              := javac
JAR             := jar
SRC             := src
BIN             := bin
LIB             := ../files/lib

JFLAGS          := -Xlint:all -encoding UTF-8 -nowarn -source 8 -target 8

# Source files — standard MIDP only
MIDP_SRCS       := $(shell find $(SRC)/javax -name "*.java" 2>/dev/null)
M3G_SRCS        := $(shell find $(SRC)/javax/microedition/m3g -name "*.java" 2>/dev/null)

.PHONY: all midp m3g clean

all: midp m3g

midp: $(BIN)/j2me.jar

m3g: $(BIN)/m3g.jar

$(BIN)/j2me.jar: $(MIDP_SRCS)
	@mkdir -p $(BIN)/midp
	@echo "Compiling standard MIDP 2.0 API..."
	@$(JC) $(JFLAGS) -cp "$(LIB)/flintos.midp.jar" -d $(BIN)/midp $(MIDP_SRCS)
	@echo "Packaging j2me.jar..."
	@$(JAR) --create --file $@ -C $(BIN)/midp .

$(BIN)/m3g.jar: $(M3G_SRCS) $(BIN)/j2me.jar
	@mkdir -p $(BIN)/m3g
	@echo "Compiling M3G 3D API..."
	@$(JC) $(JFLAGS) -cp "$(LIB)/flintos.midp.jar;$(BIN)/midp" -d $(BIN)/m3g $(M3G_SRCS)
	@echo "Packaging m3g.jar..."
	@$(JAR) --create --file $@ -C $(BIN)/m3g .

clean:
	@rm -rf $(BIN)
	@echo "Clean complete"
