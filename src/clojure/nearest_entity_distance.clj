(ns clojure.nearest-entity-distance)

(defn f [this faction]
  (-> this faction :distance))
