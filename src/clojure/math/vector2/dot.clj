(ns clojure.math.vector2.dot)

(defn f
  [[this-x this-y]
   [x y]]
  (+ (* this-x x)
     (* this-y y)))
