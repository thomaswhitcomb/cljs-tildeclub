(ns tildeclub.cljs.core
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [tildeclub.cljs.gol :as gol]
            [tildeclub.cljs.dom :as dom]
            [cljs.reader :as rdr]))

;; enable cljs to print to the JS console of the browser
(enable-console-print!)

(defn app-content []
  [:div {:class "container"}
   [:hr]
   [:div {:class "control-div"}
    [:button {:id "myBtn" :type "button" :class "btn btn-lg btn-success" :onclick "tildeclub.cljs.core.run_gol()"} "Run"]
    [:div
     [:a {:href "./help/universe.html"} "Create your own universe..."]
     [:textarea {:id "golUniverse" :rows "10" } "{[3,2] :l [3,3] :l [2,3] :l [7,2] :l [8,2] :l,[9,2] :l [8 4] :l}"]]]
   [:div {:id "golOutput" :class "table-div"}]
   [:input {:id "golIteration" :type "text" :placeholder "evolution cycles" :value "1"}]])


(defn set!-content []
  (set!  (.-innerHTML (dom/get-element-by-id "appPanel")) (html (app-content))))

(defn keyup [e]
  (if (= (.-keyCode e) 13)
    (do
      (.call (aget  e "preventDefault") e )
      (.call (aget (dom/get-element-by-id "myBtn") "click") (dom/get-element-by-id "myBtn")))))

(defn render []
  (do
      (set!-content)
      (dom/add-event-listener "golIteration" "keyup" keyup)
      (dom/add-event-listener "golUniverse" "keyup" keyup)))

(defn -main [& parms]
  (println "main is called" parms))

(defn run_gol []
  (set!
    (.-value (dom/get-element-by-id "golUniverse"))
    (gol/run "golOutput" "golUniverse" "golIteration")))

;; print to the console
(println "Hello Fred")
(println "navbarNavDropdown" (dom/get-element-by-id "navbarNavDropdown"))
(println "nav-item" (dom/get-element-by-class-name "nav-item"))
(println "body:" (dom/get-element-by-tag-name "body"))
(js/console.log gol/square)
;(gol/select-alive (gol/fast-forward 130 gol/diehard gol/display))
