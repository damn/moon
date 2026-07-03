(ns scene2d.ui.table.add-rows
  (:require [clojure.gdx.table.add :as add]
            [clojure.gdx.table.row :as row]
            [scene2d.ui.table.add-cell :refer [add-cell!]]))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
        (map? props-or-actor)
        (add-cell! table props-or-actor)

        ; TODO Remove else case
        :else (add/f table props-or-actor)))
    (row/f table))
  table)
