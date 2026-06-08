(ns clojure.math.vector2.scale)

(defn f [[x y] scalar]
  [(* x scalar)
   (* y scalar)])
