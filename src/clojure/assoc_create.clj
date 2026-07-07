(ns clojure.assoc-create)

(defn f [m k f]
  (assoc m k (f m)))
