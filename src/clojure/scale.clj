(ns clojure.scale)

(defn f [[x y] scalar]
  [(* x scalar)
   (* y scalar)])
