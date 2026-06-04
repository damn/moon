(ns moon.ui.inventory-window.create-cell
  (:require [clojure.gdx.scene2d.actor.hit :refer [hit]]
            [clojure.gdx.scene2d.actor.stage-local-coordinates :refer [stage->local-coordinates]]
            [clojure.gdx.scene2d.actor.get-x :refer [get-x]]
            [clojure.gdx.scene2d.actor.get-y :refer [get-y]]
            [clojure.gdx.scene2d.actor.get-parent :refer [get-parent]]
            [clojure.gdx.scene2d.actor.get-user-object :refer [get-user-object]]
            [clojure.gdx.scene2d.actor.get-stage :refer [get-stage]]
            [clojure.gdx.scene2d.actor.set-name :refer [set-name!]]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.actor.set-user-object :refer [set-user-object!]]
            [clojure.gdx.scene2d.group.add-actor :refer [add-actors!]]
            [clojure.gdx.scene2d.event.get-stage :as event]
            [clojure.gdx.scene2d.ui.widget :as widget]
            [game.ctx.do :refer [do!]]
            [game.ctx.draw :refer [draw!]]
            [game.state :as state]
            [clojure.gdx.math.vector2 :as vector2]
            [clojure.gdx.scene2d.utils.click-listener :as click-listener]
            [clojure.gdx.scene2d.ui.image :as image]
            [clojure.gdx.scene2d.ui.stack :as stack]
            entity.state.clicked-inventory-cell.player-item-on-cursor
            entity.state.clicked-inventory-cell.player-idle))

(def k->fn
  {
   :player-item-on-cursor entity.state.clicked-inventory-cell.player-item-on-cursor/f
   :player-idle entity.state.clicked-inventory-cell.player-idle/f
   }
  )

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
                         (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (event/get-stage event))
                               entity @player-eid
                               state-k (:state (:entity/fsm entity))]
                           (do! ctx
                                (if-let [f (k->fn state-k)]
                                  (f player-eid cell)
                                  nil))))))
       (set-name! "inventory-cell")
       (set-user-object! cell))}))
