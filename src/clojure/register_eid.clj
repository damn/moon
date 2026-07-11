(ns clojure.register-eid
  (:require [moon.content-grid :as content-grid]
            [clojure.set-occupied-cells :refer [set-occupied-cells!]]
            [clojure.set-touched-cells :refer [set-touched-cells!]]
            [clojure.grid.valid-position :refer [valid-position?]]))

(defn do! [ctx eid]
  (assert (and (not (contains? @eid :entity/id))))
  (let [id (swap! (:ctx/id-counter ctx) inc)]
    (assert (number? id))
    (swap! eid assoc :entity/id id)
    (swap! (:ctx/entity-ids ctx) assoc id eid))

  (assert (:entity/body @eid)) ; -< inside content grid
  (content-grid/update-entity! (:ctx/content-grid ctx) eid)

  (assert (:entity/body @eid)) ; <- inside the grid add fn ?
  (when (:body/collides? (:entity/body @eid))
    (assert (valid-position? (:ctx/grid ctx) (:entity/body @eid) (:entity/id @eid))))
  (set-touched-cells! (:ctx/grid ctx) eid)
  (when (:body/collides? (:entity/body @eid)) ; entity/collides? separate fooziboosh, no 'when' just a callback?
    (set-occupied-cells! (:ctx/grid ctx) eid))
  nil)
