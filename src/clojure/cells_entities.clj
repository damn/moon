(ns clojure.cells-entities)

(defn f [cells]
  (into #{} (mapcat :entities) cells))
