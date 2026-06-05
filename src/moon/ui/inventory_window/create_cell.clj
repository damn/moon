(ns moon.ui.inventory-window.create-cell
  (:require [clojure.scene2d.actor.hit :refer [hit]]
            [clojure.scene2d.actor.stage-local-coordinates :refer [stage->local-coordinates]]
            [clojure.scene2d.actor.get-x :refer [get-x]]
            [clojure.scene2d.actor.get-y :refer [get-y]]
            [clojure.scene2d.actor.get-parent :refer [get-parent]]
            [clojure.scene2d.actor.get-user-object :refer [get-user-object]]
            [clojure.scene2d.actor.get-stage :refer [get-stage]]
            [clojure.scene2d.actor.set-name :refer [set-name!]]
            [clojure.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.scene2d.actor.set-user-object :refer [set-user-object!]]
            [clojure.scene2d.group.add-actor :refer [add-actors!]]
            [clojure.scene2d.event.get-stage :as event]
            [clojure.scene2d.ui.widget :as widget]
            [game.ctx.do :refer [do!]]
            [game.ctx.draw :refer [draw!]]
            [game.state :as state]
            [clojure.gdx.math.vector2 :as vector2]
            [clojure.scene2d.utils.click-listener :as click-listener]
            [clojure.scene2d.ui.image :as image]
            [clojure.scene2d.ui.stack :as stack]))

(defn ->cell [slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (doto (stack/create)
       (add-actors! [(widget/create
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
                                                                (vector2/->clj
                                                                 (stage->local-coordinates this
                                                                                           (vector2/create ui-mouse-position)))
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
