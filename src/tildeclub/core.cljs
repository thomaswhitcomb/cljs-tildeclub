(ns tildeclub.core
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [tildeclub.gol :as gol]
            [cljs.reader :as rdr]
            [left-pad]))

;; enable cljs to print to the JS console of the browser
(enable-console-print!)

(defn get-element-by-id [id]
  (.call (aget js/document "getElementById") js/document id))

(defn get-element-by-class-name [cn]
  (.call (aget js/document "getElementsByClassName") js/document cn))

(defn get-element-by-tag-name [tn]
  (.call (aget js/document "getElementsByTagName") js/document tn))

(defn call-handler [t nm & parms]
  (let [tf (get-element-by-id t)]
    (. (aget tf nm) apply tf (clj->js parms))))

(defn add-event-listener [t e f]
  (call-handler t "addEventListener" e f))

(defn app-content []
  [:div
   [:p "Until I do, you can entertain yourself by running Conmay's Game of Life.  I know the UI sucks but the game is functional and I'll make it look better soon.  Did I say this is written in ClojureScript? "]
   [:hr]
   [:div {:class "form-group"}
     [:label {:for "comment" :text-align "left"}"Universe State"]
     [:textarea {:id "golUniverse" :class "form-control" :rows="5" :id="comment"} "{[3,2] :l [3,3] :l [2,3] :l [7,2] :l [8,2] :l,[9,2] :l [8 4] :l}"]]
   [:div {:class "control-div"}
    [:input {:id "golIteration" :type "text" :placeholder "evolution cycles" :value "1"}]
    [:br]
    [:button {:id "myBtn" :class "btn btn-success" :onclick "tildeclub.core.run_gol()"} "Run Game of Life"]]
   [:div {:id "golOutput" :class "table-div"}]])


(defn set!-content []
  (set!  (.-innerHTML (get-element-by-id "appPanel")) (html (app-content))))

(defn keyup [e]
  (if (= (.-keyCode e) 13)
    (do
      (.call (aget  e "preventDefault") e )
      (.call (aget (get-element-by-id "myBtn") "click") (get-element-by-id "myBtn")))))

(defn render []
  (do
      (set!-content)
      (add-event-listener "golIteration" "keyup" keyup)
      (add-event-listener "golUniverse" "keyup" keyup)))

(defn -main [& parms]
  (println "main is called" parms))

(defn run_gol []
  (set!
    (.-value (get-element-by-id "golUniverse"))
    (gol/run "golOutput" "golUniverse" "golIteration")))

;; print to the console
(println "Hello Fred")
(println "navbarNavDropdown" (get-element-by-id "navbarNavDropdown"))
(println "nav-item" (get-element-by-class-name "nav-item"))
(println "body:" (get-element-by-tag-name "body"))
(js/console.log  (left-pad 42 5 0))
(js/console.log gol/square)
;(gol/select-alive (gol/fast-forward 130 gol/diehard gol/display))
