(ns clojure.v2.dot)

(defn f
  [[this-x this-y]
   [x y]]
  (+ (* this-x x)
     (* this-y y)))
