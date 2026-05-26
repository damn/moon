(ns game.render.update-mouseover-eid
  (:require [clojure.scene2d.stage :as stage]
            [game.ctx :as ctx]
            [moon.grid :as grid]
            [moon.order :as order]
            [moon.raycaster :as raycaster]))

(defn step
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/grid
           ctx/raycaster
           ctx/render-z-order
           ctx/world-mouse-position]
    :as ctx}]
  (let [mouseover-actor (stage/mouseover-actor stage (ctx/mouse-position ctx))
        position world-mouse-position
        new-eid (if mouseover-actor
                  nil
                  (let [player @player-eid
                        hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                     (grid/point->entities grid position))]
                    (->> render-z-order
                         (order/sort-by-order hits #(:body/z-order (:entity/body @%)))
                         reverse
                         (filter #(raycaster/line-of-sight? raycaster player @%))
                         first)))]
    (when mouseover-eid
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc ctx :ctx/mouseover-eid new-eid)))
