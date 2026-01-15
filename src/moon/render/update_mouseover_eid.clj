(ns moon.render.update-mouseover-eid
  (:require [moon.input :as input]
            [moon.ui :as ui]
            [moon.world.grid :as grid]
            [moon.world :as world]
            [moon.utils :as utils]))

(defn do!
  [{:keys [ctx/input
           ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world
           ctx/world-mouse-position]
    :as ctx}]
  (let [mouseover-actor (ui/mouseover-actor stage (input/mouse-position input))
        {:keys [world/grid
                world/raycaster
                world/render-z-order]} world
        position world-mouse-position
        new-eid (if mouseover-actor
                  nil
                  (let [player @player-eid
                        hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                     (grid/point->entities grid position))]
                    (->> render-z-order
                         (utils/sort-by-order hits #(:body/z-order (:entity/body @%)))
                         reverse
                         (filter #(world/line-of-sight? world player @%))
                         first)))]
    (when mouseover-eid
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc ctx :ctx/mouseover-eid new-eid)))
