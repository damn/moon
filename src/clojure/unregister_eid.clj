(ns clojure.unregister-eid
  (:require [clojure.remove-entity :as remove-entity]
            [clojure.remove-from-touched-cells :refer [remove-from-touched-cells!]]
            [clojure.remove-from-occupied-cells :refer [remove-from-occupied-cells!]]))

(defn do!
  [{:keys [ctx/content-grid
           ctx/entity-ids
           ctx/grid]
    :as ctx}
   eid]
  (let [id (:entity/id @eid)]
    (assert (contains? @entity-ids id))
    (swap! entity-ids dissoc id))
  (remove-entity/f! content-grid eid)
  (remove-from-touched-cells! grid eid)
  (when (:body/collides? (:entity/body @eid))
    (remove-from-occupied-cells! grid eid))
  nil)
