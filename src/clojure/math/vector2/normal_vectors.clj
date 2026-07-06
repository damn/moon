(ns clojure.math.vector2.normal-vectors)

(defn f [[x y]]
  [[(- (float y))         x]
   [          y (- (float x))]])
