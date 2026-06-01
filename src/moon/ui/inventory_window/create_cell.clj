(ns moon.ui.inventory-window.create-cell
  (:require [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.ui.widget :as widget]
            [game.ctx.do :refer [do!]]
            [game.ctx.draw :refer [draw!]]
            [game.state :as state]
            [gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scene2d.actor.x :refer [actor-x]]
            [clojure.gdx.scene2d.actor.y :refer [actor-y]]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.stack :as stack]
            entity.state.clicked-inventory-cell.player-item-on-cursor
            entity.state.clicked-inventory-cell.player-idle
            ))

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
     (stack/create
      {:actor/name "inventory-cell"
       :actor/user-object cell
       :actor/listeners {:listener/click (fn [event _x _y]
                                           (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (event/stage event))
                                                 entity @player-eid
                                                 state-k (:state (:entity/fsm entity))]
                                             (do! ctx
                                                  (if-let [f (k->fn state-k)]
                                                    (f player-eid cell)
                                                    nil))))}
       :group/actors [(widget/create
                       {:draw! (fn [this _batch _parent-alpha]
                                 (when-let [stage (actor/stage this)]
                                   (let [{:keys [ctx/player-eid
                                                 ctx/ui-mouse-position]
                                          :as ctx} (:stage/ctx stage)]
                                     (draw! ctx
                                            (draw-cell-rect @player-eid
                                                            (actor-x this)
                                                            (actor-y this)
                                                            (actor/hit this
                                                                       (actor/stage->local-coordinates this ui-mouse-position)
                                                                       true)
                                                            (actor/user-object (actor/parent this)))))))})
                      (image/create
                       {:content background-drawable
                        :actor/name "image-widget"
                        :actor/user-object {:background-drawable background-drawable
                                            :cell-size cell-size}})]})}))
