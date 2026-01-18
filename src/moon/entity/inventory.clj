(ns moon.entity.inventory
  (:require [clojure.grid2d :as g2d]
            [moon.inventory :as inventory]))

(defn after-create
  [[_k items] eid _ctx]
  (cons [:tx/assoc eid :entity/inventory (->> inventory/empty-inventory
                                              (map (fn [[slot [width height]]]
                                                     [slot (g2d/create-grid width height (constantly nil))]))
                                              (into {}))]
        (for [item items]
          [:tx/pickup-item eid item])))
