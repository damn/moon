(ns clojure.get-4-neighbours)

(defn get-4-neighbours [[x y]]
  [[(inc x) y]
   [(dec x) y]
   [x (inc y)]
   [x (dec y)]])
