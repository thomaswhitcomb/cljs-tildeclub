NAMESPACE=tildeclub
ODIR=js
SDIR=src/$(NAMESPACE)
TDIR=test/$(NAMESPACE)

JSFILES = $(patsubst $(SDIR)/%.cljs,$(ODIR)/%.js, $(wildcard $(SDIR)/*.cljs))
TESTFILES = $(patsubst $(TDIR)/%.cljs,$(ODIR)/%.js, $(wildcard $(TDIR)/*.cljs))

all: clean build test

.PHONY: build
build:
	clj -m cljs.main  --output-dir js -c tildeclub.cljs.core
	clj -m cljs.main  --target browser --output-dir js --output-to js/shim.js -c tildeclub.shim
	clj -m cljs.main  -t node --output-to js/test_runner.js -c tildeclub.test-runner


.PHONY: clean
clean:
	rm -Rf js/*
	rm -Rf node_modules/*
	rm -Rf package*json
.PHONY: test
test:
	#clj -m cljs.main -t node -o js/test-runner.js -c tildeclub.test-runner
	node  js/test_runner.js

.PHONY: repl
repl:
	clj -m cljs.main --repl-env node
