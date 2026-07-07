(ns clojure.normal-vectors)

(defn f [[x y]]
  [[(- (float y))         x]
   [          y (- (float x))]])
