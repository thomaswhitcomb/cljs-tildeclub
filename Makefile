DEPLOY_DIR=.
NAMESPACE=tildeclub
ODIR=js
SDIR=src/$(NAMESPACE)

JSFILES = $(patsubst $(SDIR)/%.cljs,$(ODIR)/%.js, $(wildcard $(SDIR)/*.cljs))

$(ODIR)/%.js: $(SDIR)/%.cljs $(SDIR)/%.edn
	clj -m cljs.main -co $(SDIR)/$(basename $(notdir $@)).edn --output-to js/$(notdir $@) --output-dir $(ODIR) -c $(NAMESPACE).$(basename $(notdir $@))

all: $(JSFILES)

.PHONY: clean
clean:
	rm -Rf ${DEPLOY_DIR}/js/*
	rm -Rf node_modules/*
	rm -Rf package*json

.PHONY: repl
repl:
	clj -m cljs.main --repl-env node
