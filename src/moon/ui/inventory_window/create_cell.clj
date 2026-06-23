(ns moon.ui.inventory-window.create-cell
  (:require [gdx.math.vector2.clojurize :as clojurize]
            [scene2d.actor.hit :refer [hit]]
            [scene2d.actor.stage-local-coordinates :refer [stage->local-coordinates]]
            [scene2d.actor.get-x :refer [get-x]]
            [scene2d.actor.get-y :refer [get-y]]
            [scene2d.actor.get-parent :refer [get-parent]]
            [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.actor.get-stage :as actor-stage]
            [scene2d.event.get-stage :as get-stage]
            [scene2d.actor.set-name :refer [set-name!]]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.actor.set-user-object :refer [set-user-object!]]
            [scene2d.group.add-actors :refer [add-actors!]]
            [ui.widget :as widget]
            [ctx.do :refer [do!]]
            [ctx.draw :refer [draw!]]
            [game.state :as state]
            [scene2d.click-listener :as click-listener]
            [ui.image :as image]
            [ui.stack :as stack]
            [gdx.math.vector2 :as vector2]))

(defn ->cell [slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (doto (stack/create)
       (add-actors! [(widget/f
                      {:draw! (fn [this _batch _parent-alpha]
                                (when-let [stage (actor-stage/f this)]
                                  (let [{:keys [ctx/player-eid
                                                ctx/ui-mouse-position]
                                         :as ctx} (:stage/ctx stage)]
                                    (draw! ctx
                                           (draw-cell-rect @player-eid
                                                           (get-x this)
                                                           (get-y this)
                                                           (hit this
                                                                (clojurize/f
                                                                 (stage->local-coordinates this
                                                                                           (vector2/f ui-mouse-position)))
                                                                true)
                                                           (get-user-object (get-parent this)))))))})
                     (doto (image/create background-drawable)
                       (set-name! "image-widget")
                       (set-user-object! {:background-drawable background-drawable
                                          :cell-size cell-size}))])
       (add-listener! (click-listener/create
                       (fn [event _x _y]
                         (let [{:keys [ctx/player-eid
                                       ctx/k->clicked-inventory-cell]
                                :as ctx} (:stage/ctx (get-stage/f event))
                               entity @player-eid
                               state-k (:state (:entity/fsm entity))]
                           (do! ctx
                                (if-let [f (k->clicked-inventory-cell state-k)]
                                  (f player-eid cell)
                                  nil))))))
       (set-name! "inventory-cell")
       (set-user-object! cell))}))
