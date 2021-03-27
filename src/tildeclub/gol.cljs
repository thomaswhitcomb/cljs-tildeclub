(ns tildeclub.gol
  (:require [cljs.reader :as rdr]))

(def oscillator-blinker {[2 2]:l [2 3]:l [2 4]:l })
(def oscillator-beacon {[2 5]:l [2 6]:l [3 5]:l [3 6]:l [4 3]:l [4 4]:l [5 3]:l [5 4]:l  })
(def spaceship-glider {[1 1]:l [2 1]:l [3 1]:l [3 2]:l [2 3]:l})
(def oscillator-toad {[1,2] :l,[2,2] :l,[2,3] :l,[3,3] :l, [3,2] :l,[4,3] :l})
(def diehard {[3,2] :l [3,3] :l [2,3] :l [7,2] :l  [8,2] :l,[9,2] :l [8 4] :l}) ; Goes away after 130 generations
(def blinker1-on {[1,0] :l,[1,1] :l,[1,2] :l})
(def blinker1-off {[1 1] :l [2 1] :l [0 1] :l})
(def blinker2-on {[1,2] :l,[2,2] :l,[2,3] :l,[3,3] :l, [3,2] :l,[4,3] :l})
(def blinker2-off {[4 3] :l, [3 4] :l, [4 2] :l, [1 3] :l, [2 1] :l, [1 2] :l})

(def square \u25FB)
(defn get-element-by-id [id]
  (.call (aget js/document "getElementById") js/document id))

(defn addhtml  [id html]
  (set!  (.-innerHTML (get-element-by-id id)) html))

(defn build-table-fn [universe maxx margin]
 (fn [accum [x y]]
   (let [ res
         (cond
           (= (universe [x y]) :l) (str accum  "<td class='live'></td>")
           :else (str accum "<td class='dead'></td>"))]
     (if (= x (+ maxx (- margin 1)))
       (str accum "</tr><tr>")
       res))))

(defn display
  [html-ele universe]
  (def margin 3)
  (def minx (apply min (map (fn [[x y]] x) (keys universe))))
  (def maxx (apply max (map (fn [[x y]] x) (keys universe))))
  (def miny (apply min (map (fn [[x y]] y) (keys universe))))
  (def maxy (apply max (map (fn [[x y]] y) (keys universe))))
  ;(str "<p> "minx " " maxx " " miny " " maxy "</p>" (.-innerHTML (get-element-by-id html-ele))))
  (let [span (for [ y (range (+ maxy margin) (- miny margin) -1)
                   x (range (- minx margin) (+ maxx margin) 1) ]
               [x y])
        html (str
               (reduce (build-table-fn universe maxx margin) "<table><tr>" span)
               "</tr></table")]
    (println html)
    (addhtml html-ele html)))


(defn surrounding-peers
  [x y]
  [[(+ x 1) y]
   [(- x 1) y]
   [x (+ y 1)]
   [x (- y 1)]
   [(- x 1) (- y 1)]
   [(+ x 1) (+ y 1)]
   [(- x 1) (+ y 1)]
   [(+ x 1) (- y 1)]])

(defn live-neighbours-around
  [[x y] universe]
  (filter #(= (universe % :d) :l) (surrounding-peers x y)))

(defn evolve
  [[k v] universe]
  (let [live-count (count (live-neighbours-around k universe))]
    (cond
     (and (= v :l)
          (or (= live-count 2) (= live-count 3))) [k :l]
     (= v :l) [k :d]
     (= live-count 3) [k :l]
     :else [k :d])))

(defn pad-cell [[x y]]
  (apply merge
    (for [a (surrounding-peers x y) ] {a :d})))

(defn is-alive [[k v]]
  (= v :l))

(defn select-alive
  [universe]
    (into {} (filter is-alive universe)))

(defn pad-the-board [universe]
  (let [
        alive (select-alive universe)
        deads (->>
                (map (fn [[k v]] (pad-cell k)) alive)
                (apply merge))
       ]
     (merge deads alive)))

(defn  fast-forward[tick universe viewer ]
  (loop [tick1 tick universe1 universe]
    (viewer universe1)
    (let [padded-universe (pad-the-board universe1)]
      (cond
        (<= tick1 0) padded-universe
        :else (recur
                (- tick1 1)
                (into {} (map (fn [kv](evolve kv padded-universe)) padded-universe)))
      ))))

(defn run [html-ele html-universe html-iteration]
  (str
    (let [p (partial display html-ele)
          n (js/parseInt (.-value (get-element-by-id html-iteration)))
          u (.-value (get-element-by-id html-universe))]
      (select-alive (fast-forward n (rdr/read-string u) p )))))


;(is (= (pad-the-board {[2 2]:l [2 3]:l [2 4]:l })
;       {[2 2] :l, [2 3] :l, [2 5] :d, [3 3] :d, [1 1] :d, [3 4] :d, [1 4] :d, [1 3] :d, [1 5] :d, [2 4] :l, [3 1] :d, [2 1] :d, [1 2] :d, [3 5] :d, [3 2] :d}))

;(is (=  (fast-forward 100 {[1 1]:l [2 1]:l [3 1]:l [3 2]:l [2 3]:l} (fn [_] nil))
;       {[27 -23] :d, [29 -23] :d, [27 -25] :d, [26 -21] :d, [27 -22] :l, [29 -22] :d, [28 -23] :l, [27 -21] :d, [25 -25] :d, [25 -23] :d, [26 -25] :d, [25 -24] :d, [28 -21] :d, [28 -24] :l, [26 -22] :d, [26 -24] :l, [27 -24] :l, [26 -23] :d, [29 -24] :d, [28 -22] :d, [28 -25] :d, [29 -25] :d}))

;(is (= (live-neighbours-around  [0 0]  {[1 1]:l [2 1]:l [3 1]:l [3 2]:l [2 3]:l}) [[1 1]] ))
;(is (= (live-neighbours-around  [1 2]  {[1 1]:l [2 1]:l [3 1]:l [3 2]:l [2 3]:l}) [[1 1] [2 3] [2 1]] ))
;(is (= (select-alive (fast-forward 1 blinker1-on (fn [_] nil)))) blinker1-off)
;(is (= (select-alive (fast-forward 2 blinker1-on (fn [_] nil)))) blinker1-on)
;(is (= (select-alive (fast-forward 3 blinker1-on (fn [_] nil)))) blinker1-off)
;(is (= (select-alive (fast-forward 4 blinker1-on (fn [_] nil)))) blinker1-on)
;(is (= (select-alive (fast-forward 1 blinker2-on (fn [_] nil)))) blinker2-off)
;(is (= (select-alive (fast-forward 2 blinker2-on (fn [_] nil)))) blinker2-on)
;(is (= (select-alive (fast-forward 3 blinker2-on (fn [_] nil)))) blinker2-off)
;(is (= (select-alive (fast-forward 4 blinker2-on (fn [_] nil)))) blinker2-on)
;(select-alive (fast-forward 130 diehard display))
