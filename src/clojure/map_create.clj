(ns clojure.map-create)

(defn f [m k [f & params]]
  (assoc m k (apply f m params)))
