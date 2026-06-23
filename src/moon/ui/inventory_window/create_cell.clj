(ns moon.ui.inventory-window.create-cell
  (:require [gdl.vector2.clojurize :as clojurize]
            [gdl.hit :refer [hit]]
            [gdl.stage-local-coordinates :refer [stage->local-coordinates]]
            [gdl.get-x :refer [get-x]]
            [gdl.get-y :refer [get-y]]
            [gdl.get-parent :refer [get-parent]]
            [gdl.get-user-object :refer [get-user-object]]
            [gdl.get-stage :refer [get-stage]]
            [gdl.set-name :refer [set-name!]]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [gdl.set-user-object :refer [set-user-object!]]
            [group.add-actors :refer [add-actors!]]
            [gdl.get-stage :as event]
            [ui.widget :as widget]
            [game.ctx.do :refer [do!]]
            [game.ctx.draw :refer [draw!]]
            [game.state :as state]
            [gdl.click-listener :as click-listener]
            [ui.image :as image]
            [ui.stack :as stack]
            [gdl.vector2 :as vector2]))

(defn ->cell [slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (doto (stack/create)
       (add-actors! [(widget/f
                      {:draw! (fn [this _batch _parent-alpha]
                                (when-let [stage (get-stage this)]
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
                                :as ctx} (:stage/ctx (event/get-stage event))
                               entity @player-eid
                               state-k (:state (:entity/fsm entity))]
                           (do! ctx
                                (if-let [f (k->clicked-inventory-cell state-k)]
                                  (f player-eid cell)
                                  nil))))))
       (set-name! "inventory-cell")
       (set-user-object! cell))}))
