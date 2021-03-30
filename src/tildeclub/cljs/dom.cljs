(ns tildeclub.cljs.dom)

(defn get-element-by-id [id]
  (.call (aget js/document "getElementById") js/document id))

(defn add-html  [id html]
  (set!  (.-innerHTML (get-element-by-id id)) html))

(defn get-element-by-class-name [cn]
  (.call (aget js/document "getElementsByClassName") js/document cn))

(defn get-element-by-tag-name [tn]
  (.call (aget js/document "getElementsByTagName") js/document tn))

(defn call-handler [t nm & parms]
  (let [tf (get-element-by-id t)]
    (. (aget tf nm) apply tf (clj->js parms))))

(defn add-event-listener [t e f]
  (call-handler t "addEventListener" e f))
