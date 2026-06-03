(ns entity.after-create.inventory
  (:require [clojure.grid2d :as g2d]))

(defn f
  [items eid _ctx]
  (cons [:tx/assoc eid :entity/inventory (->> #:inventory.slot{:bag      [6 4]
                                                               :weapon   [1 1]
                                                               :shield   [1 1]
                                                               :helm     [1 1]
                                                               :chest    [1 1]
                                                               :leg      [1 1]
                                                               :glove    [1 1]
                                                               :boot     [1 1]
                                                               :cloak    [1 1]
                                                               :necklace [1 1]
                                                               :rings    [2 1]}
                                              (map (fn [[slot [width height]]]
                                                     [slot (g2d/create-grid width height (constantly nil))]))
                                              (into {}))]
        (for [item items]
          [:tx/pickup-item eid item])))
