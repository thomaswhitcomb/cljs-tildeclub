(ns tildeclub.shim
  (:require
    [tildeclub.cljs.core :as core]
    [tildeclub.cljs.dom :as dom]
    [tildeclub.cljs.gol :as gol]))

(defn render []
  (core/render
    {:universe "golUniverse"
     :iterations "golIteration"}))

(defn run_gol []
  (set!
     (.-value (dom/get-element-by-id "golUniverse"))
     (gol/run
       {:output "golOutput"
        :universe "golUniverse"})))

