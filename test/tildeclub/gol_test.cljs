(ns tildeclub.gol_test
  (:require
    [cljs.test     :refer-macros [deftest is]]
    [tildeclub.cljs.gol :as gol]))

(deftest test-pad-the-board
  (is (= (gol/pad-the-board {[2 2]:l [2 3]:l [2 4]:l }) {[2 2] :l, [2 3] :l, [2 5] :d, [3 3] :d, [1 1] :d, [3 4] :d, [1 4] :d, [1 3] :d, [1 5] :d, [2 4] :l, [3 1] :d, [2 1] :d, [1 2] :d, [3 5] :d, [3 2] :d})))

(deftest test-fast-forward
  (is (=  (gol/fast-forward 100 {[1 1]:l [2 1]:l [3 1]:l [3 2]:l [2 3]:l} )
       {[27 -23] :d, [29 -23] :d, [27 -25] :d, [26 -21] :d, [27 -22] :l, [29 -22] :d, [28 -23] :l, [27 -21] :d, [25 -25] :d, [25 -23] :d, [26 -25] :d, [25 -24] :d, [28 -21] :d, [28 -24] :l, [26 -22] :d, [26 -24] :l, [27 -24] :l, [26 -23] :d, [29 -24] :d, [28 -22] :d, [28 -25] :d, [29 -25] :d}))
  (is (= (gol/live-neighbours-around  [0 0]  {[1 1]:l [2 1]:l [3 1]:l [3 2]:l [2 3]:l}) [[1 1]] ))
  (is (= (gol/live-neighbours-around  [1 2]  {[1 1]:l [2 1]:l [3 1]:l [3 2]:l [2 3]:l}) [[1 1] [2 3] [2 1]] )))

(deftest test-corners
  (is (= (gol/corners {[3 2] :l}) {:minx 3 :maxx 3 :miny 2 :maxy 2}))
  (is (= (gol/corners {[3 2] :l [7 1] :l}) {:minx 3 :maxx 7 :miny 1 :maxy 2})))

(deftest test-full-runs
  (is (= (gol/select-alive (gol/fast-forward 1 gol/blinker1-on )) gol/blinker1-off))
  (is (= (gol/select-alive (gol/fast-forward 2 gol/blinker1-on )) gol/blinker1-on))
  (is (= (gol/select-alive (gol/fast-forward 3 gol/blinker1-on )) gol/blinker1-off))
  (is (= (gol/select-alive (gol/fast-forward 4 gol/blinker1-on )) gol/blinker1-on))
  (is (= (gol/select-alive (gol/fast-forward 1 gol/blinker2-on )) gol/blinker2-off))
  (is (= (gol/select-alive (gol/fast-forward 2 gol/blinker2-on )) gol/blinker2-on))
  (is (= (gol/select-alive (gol/fast-forward 3 gol/blinker2-on )) gol/blinker2-off))
  (is (= (gol/select-alive (gol/fast-forward 4 gol/blinker2-on )) gol/blinker2-on))
  (is (= (gol/select-alive (gol/fast-forward 10 gol/diehard )) {[8 -1] :l, [2 2] :l, [3 2] :l, [8 5] :l, [10 1] :l, [7 2] :l, [2 3] :l, [8 1] :l, [5 2] :l, [9 0] :l, [8 3] :l, [7 0] :l, [7 4] :l, [8 4] :l, [3 3] :l, [10 3] :l, [9 4] :l, [6 3] :l, [10 2] :l, [6 1] :l, [8 0] :l, [11 2] :l, [6 2] :l, [9 2] :l})))
