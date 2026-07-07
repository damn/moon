(ns clojure.add-rows
  (:require [clojure.table :as table]
            [clojure.add-cell :refer [add-cell!]]))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
        (map? props-or-actor)
        (add-cell! table props-or-actor)

        ; TODO Remove else case
        :else (table/add! table props-or-actor)))
    (table/row! table))
  table)
