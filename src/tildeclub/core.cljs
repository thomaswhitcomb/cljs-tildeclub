(ns tildeclub.core
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [tildeclub.gol :as gol]
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
   [:p "Until I do, you can entertain yourself by creating fibonacci sequences. Enter the size of the sequence."]
   [:input {:id "myInput" :type "text" :placeholder "10" :value "10"}]
   [:button {:id "myBtn" :class "btn btn-success" :onclick "tildeclub.core.fib_()"} "Compute Sequence"]
   [:div {:id "myOutput"}]])


(defn fib
  ([n]
   (cond
     (= n 0) (vector)
     (= n 1) (vector 0)
     (= n 2) (vector 0 1)
     :else (fib (- n 2) (vector 0 1))))
  ([n v]
   (cond
     (= n 0) v
     :else
     (fib (dec n) (conj v (+ (peek v) (peek (pop v))))))))

(defn fib_ [i o]
  (let [res (fib (js/parseInt (.-value (get-element-by-id "myInput"))))]
    (set! (.-innerText (get-element-by-id "myOutput")) (str res))))

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
      (add-event-listener "myInput" "keyup" keyup)))

(defn -main [& parms]
  (println "main is called" parms))

;; print to the console
(println "Hello Fred")
(println "navbarNavDropdown" (get-element-by-id "navbarNavDropdown"))
(println "nav-item" (get-element-by-class-name "nav-item"))
(println "body:" (get-element-by-tag-name "body"))
(println (left-pad 42 5 0))
(println gol/square)
