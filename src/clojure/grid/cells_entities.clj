(ns clojure.grid.cells-entities)

(defn f [cells]
  (into #{} (mapcat :entities) cells))
