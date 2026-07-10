(ns clojure.ui.table.add-rows
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [clojure.ui.table.add-cell :refer [add-cell!]]))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
        (map? props-or-actor)
        (add-cell! table props-or-actor)

        ; TODO Remove else case
        :else (table/add table props-or-actor)))
    (table/row table))
  table)
