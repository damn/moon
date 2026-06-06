(ns clojure.m.assoc-create)

(defn f [m k f]
  (assoc m k (f m)))
