# Build All
all: test-all parser-all crawler-all app-all

# Cleans all
clean: test-clean parser-clean crawler-clean app-clean

# Specific Rules
crawler-all:
	$(MAKE) -C crawler all

app-all:
	$(MAKE) -C app all

parser-all:
	$(MAKE) -C parser all

test-all:
	$(MAKE) -C test all

crawler-clean:
	$(MAKE) -C crawler clean

app-clean:
	$(MAKE) -C app clean

parser-clean:
	$(MAKE) -C parser clean

test-clean:
	$(MAKE) -C test clean
