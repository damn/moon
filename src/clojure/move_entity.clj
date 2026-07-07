(ns clojure.move-entity
  (:require [clojure.update-entity :as update-entity]
            [clojure.remove-from-occupied-cells :refer [remove-from-occupied-cells!]]
            [clojure.set-occupied-cells :refer [set-occupied-cells!]]
            [clojure.remove-from-touched-cells :refer [remove-from-touched-cells!]]
            [clojure.set-touched-cells :refer [set-touched-cells!]]))

(defn do!
  [{:keys [ctx/content-grid
           ctx/grid]}
   eid]
  (update-entity/f! content-grid eid)
  (remove-from-touched-cells! grid eid)
  (set-touched-cells! grid eid)
  (when (:body/collides? (:entity/body @eid))
    (remove-from-occupied-cells! grid eid)
    (set-occupied-cells! grid eid))
  nil)
