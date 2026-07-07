(ns clojure.nearest-entity)

(defn f [this faction]
  (-> this faction :eid))
