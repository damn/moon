(ns moon.ui.inventory-window.create-cell
  (:require [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]
            [game.ctx :as ctx]
            [game.ctx.draw :refer [draw!]]
            [game.state :as state]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.stack :as stack]))

(defn ->cell [slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (stack/create
      {:actor/name "inventory-cell"
       :actor/user-object cell
       :actor/listeners {:listener/click (fn [event _x _y]
                                           (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (event/stage event))
                                                 entity @player-eid
                                                 state-k (:state (:entity/fsm entity))]
                                             (ctx/do! ctx
                                                      (state/clicked-inventory-cell [state-k (state-k entity)]
                                                                                    player-eid
                                                                                    cell))))}
       :group/actors [(widget/create
                       {:draw! (fn [this _batch _parent-alpha]
                                 (when-let [stage (actor/stage this)]
                                   (let [{:keys [ctx/player-eid
                                                 ctx/ui-mouse-position]
                                          :as ctx} (:stage/ctx stage)]
                                     (draw! ctx
                                            (draw-cell-rect @player-eid
                                                            (actor/x this)
                                                            (actor/y this)
                                                            (actor/hit this
                                                                       (actor/stage->local-coordinates this ui-mouse-position)
                                                                       true)
                                                            (actor/user-object (actor/parent this)))))))})
                      (image/create
                       {:content background-drawable
                        :actor/name "image-widget"
                        :actor/user-object {:background-drawable background-drawable
                                            :cell-size cell-size}})]})}))
