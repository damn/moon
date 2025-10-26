(ns moon.world.tx.move-entity
  (:require [moon.world.content-grid :as content-grid]
            [moon.world.grid :as grid]
            [clojure.math.vector2 :as v]))

(defn- world-move-entity
  [{:keys [world/content-grid
           world/grid]}
   eid body direction rotate-in-movement-direction?]
  (content-grid/position-changed! content-grid eid)
  (grid/remove-from-touched-cells! grid eid)
  (grid/set-touched-cells! grid eid)
  (when (:body/collides? (:entity/body @eid))
    (grid/remove-from-occupied-cells! grid eid)
    (grid/set-occupied-cells! grid eid))
  (swap! eid assoc-in [:entity/body :body/position] (:body/position body))
  (when rotate-in-movement-direction?
    (swap! eid assoc-in [:entity/body :body/rotation-angle] (v/angle-from-vector direction)))
  nil)

(defn do!
  [{:keys [ctx/world]} & params]
  (apply world-move-entity world params))
