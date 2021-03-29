# Directories
export ROOTDIR=$(CURDIR)
export SRCDIR=$(ROOTDIR)/src
export BINDIR=$(ROOTDIR)/bin
export LIBDIR=$(ROOTDIR)/lib
export BUILDDIR=$(ROOTDIR)/build
export RSRCDIR=$(ROOTDIR)/src/main/resources

# Compiler and Compilation Flags
export JC = javac
export JCFLAGS = -g -d $(BINDIR) -classpath $(CLASSPATH)

# Execution Flags
export JAVA = java
export JFLAGS = -classpath $(CLASSPATH)

# Class Path
# Adicione qualquer classpath externo que você precise
USERCLASSPATH=.

# Criando classpath dinâmico
TMPCLASSPATH=$(USERCLASSPATH):$(realpath $(BASE)$(BINDIR)):$(RSRCDIR)
ifneq (,$(wildcard $(lib)/*))
	CLASSPATH=$(TMPCLASSPATH):$(LIBDIR)/*
endif

# Rules
default: all

run: $(BINDIR)/main/app/Main.class
	cd $(BINDIR)
	java $(JFLAGS) main.app.Main $(ARGS)

all: clean make-dirs
	$(MAKE) -C src all

make-dirs:
	@mkdir -p $(BINDIR)

clean:
	$(MAKE) -C src clean
	$(RM) -rf $(BINDIR)
	$(RM) temp/*
