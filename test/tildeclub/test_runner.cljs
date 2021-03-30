(ns tildeclub.test-runner
  (:require
    [cljs.test :refer-macros [run-tests]]
    [tildeclub.gol_test]))

(defn -main[& args]
  (run-tests 'tildeclub.gol_test))

(set! *main-cli-fn* -main)
