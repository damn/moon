(ns moon.application.render.update-mouseover-eid
  (:require [moon.input :as input]
            [moon.ui :as ui]
            [moon.world :as world]))

(defn step
  [{:keys [ctx/graphics
           ctx/input
           ctx/stage
           ctx/world]
    :as ctx}]
  (let [mouseover-actor (ui/mouseover-actor stage (input/mouse-position input))
        new-eid (if mouseover-actor
                  nil
                  (world/mouseover-entity world (:graphics/world-mouse-position graphics)))]
    (when-let [mouseover-eid (:world/mouseover-eid world)]
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc-in ctx [:ctx/world :world/mouseover-eid] new-eid)))

(comment

 ; Tests!

 (let [new-eid
       ctx (step {:ctx/graphics {:graphics/world-mouse-position [1 2]}
                  :ctx/input (reify Input
                               (mouse-position [_]
                                 [12 12]))
                  :ctx/stage (reify Ui
                               (mouseover-actor [_ position]
                                 true))
                  :ctx/world (reify World
                               (mouseover-entity [_ position]
                                 new-eid
                                 )
                               )
                  })
       (= (:ctx/world (:world/mouseover-eid ctx)) new-eid)
       ])
 )
