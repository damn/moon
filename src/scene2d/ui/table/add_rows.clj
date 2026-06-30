(ns scene2d.ui.table.add-rows
  (:require [clojure.gdx :as gdx]
            [scene2d.ui.table.add-cell :refer [add-cell!]]))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
        (map? props-or-actor)
        (add-cell! table props-or-actor)

        ; TODO Remove else case
        :else (gdx/table-add! table props-or-actor)))
    (gdx/table-row! table))
  table)
