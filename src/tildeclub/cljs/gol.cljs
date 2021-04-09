(ns tildeclub.cljs.gol
  (:require [cljs.reader :as rdr]
            [tildeclub.cljs.dom :as dom]))


(def size 6)
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
(defn is-alive [[k v]]
  (= v :l))

(defn select-alive
  [universe]
  (into {} (filter is-alive universe)))

(defn  corners [universe]
  (let [fx (fn [{minx :minx
                 maxx :maxx
                 miny :miny
                 maxy :maxy} [[x y] _]]
             {:minx (if (nil? minx) x (min minx x))
              :maxx (if (nil? maxx) x (max maxx x))
              :miny (if (nil? miny) y (min miny y))
              :maxy (if (nil? maxy) y (max maxy y))})]
    (reduce fx {:minx nil :maxx nil :miny nil :maxy nil} universe)))

(defn build-table-fn [universe minx maxx miny ]
 (fn [accum [x y]]
   (let [ accum1
         (cond
           (and (= (universe [x y]) :l) (= x minx)) (str accum "<td>" y "</td><td class='live'></td>")
           (= (universe [x y]) :l) (str accum  "<td class='live'></td>")
           (= x minx) (str accum "<td>" y "</td><td class='dead'></td>")
           :else (str accum "<td class='dead'></td>"))]
     (if (>= x maxx )
       (str accum1 "</tr><tr>")
       accum1))))

(defn compute-bounding-box [universe]
  (if (= (count universe) 0)
    [1 size 1 size]
    (let [ margin 0
           corners (corners universe)
           width (inc (- (corners :maxx) (corners :minx)))
           height (inc (- (corners :maxy) (corners :miny)))
           lmarginx (quot (- size width) 2)
           rmarginx (. js/Math round (/ (- size width) 2))
           tmarginy (quot (- size height) 2)
           bmarginy (. js/Math round (/ (- size height) 2))
           ;(assert (= size (+ lmarginx rmarginx width)))
           ;(assert (= size (+ tmarginx bmarginx height)))
           box [(- (corners :minx) lmarginx) (+ rmarginx (corners :maxx)) (- (corners :miny) tmarginy) (+ bmarginy (corners :maxy))]]
           (println "size: "  size "width: " (+ lmarginx rmarginx width) lmarginx "," rmarginx "," width "height: " (+ tmarginy bmarginy height) tmarginy "," bmarginy "," height)
      box)))

(defn xaxis [xmin xmax]
  (let [axis (reduce (fn [accum ele] (str accum "<td>" ele "</td>")) "" (range xmin (inc xmax)))]
  (str "<tr><td></td>" axis)))

(defn display
  [html-ele universe]
  (let [[minx maxx miny maxy] (compute-bounding-box universe)
        f (build-table-fn universe minx maxx miny)
        table (str "<table><tr>")
        span (for [y (range maxy (- miny 1) -1)
                   x (range minx (+ maxx 1) 1) ] [x y])
        html (str (reduce f table span) (xaxis minx maxx) "</tr></table")]
    (dom/add-html html-ele html)))

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

(defn pad-cell [[x y]]
  (apply merge
    (for [a (surrounding-peers x y) ] {a :d})))

(defn pad-the-board [universe]
  (let [
        alive (select-alive universe)
        deads (->>
                (map (fn [[k v]] (pad-cell k)) alive)
                (apply merge))
       ]
     (merge deads alive)))


(defn evolve
  [[k v] universe]
  (let [live-count (count (live-neighbours-around k universe))]
    (cond
     (and (= v :l)
          (or (= live-count 2) (= live-count 3))) [k :l]
     (= v :l) [k :d]
     (= live-count 3) [k :l]
     :else [k :d])))

(defn  fast-forward[tick universe]
  (loop [tick1 tick universe1 universe]
    universe1
    (let [padded-universe (pad-the-board universe1)]
      (cond
        (<= tick1 0) padded-universe
        :else (recur
                (- tick1 1)
                (into {} (map (fn [kv](evolve kv padded-universe)) padded-universe)))
      ))))

(defn run [{html-ele :output html-universe :universe}]
  (str
    (let [p (partial display html-ele)
          u (.-value (dom/get-element-by-id html-universe))
          edn (rdr/read-string u) ]
      (do
        (p edn)
        (select-alive (fast-forward 1 edn))))))
