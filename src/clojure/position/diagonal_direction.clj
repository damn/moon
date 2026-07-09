(ns clojure.position.diagonal-direction)

(defn f [[x y]]
  (and (not (zero? (float x)))
       (not (zero? (float y)))))
