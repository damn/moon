(ns moon.ui.inventory-window.create-cell
  (:require [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.ui.widget :as widget]
            [game.ctx.do :refer [do!]]
            [game.ctx.draw :refer [draw!]]
            [game.state :as state]
            [clojure.gdx.math.vector2 :as vector2]
            [clojure.gdx.scene2d.utils.click-listener :as click-listener]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.ui.stack :as stack]
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
     (stack/create
      {:actor/name "inventory-cell"
       :actor/user-object cell
       :actor/listeners [(click-listener/create
                          (fn [event _x _y]
                            (let [{:keys [ctx/player-eid] :as ctx} (:stage/ctx (event/stage event))
                                  entity @player-eid
                                  state-k (:state (:entity/fsm entity))]
                              (do! ctx
                                   (if-let [f (k->fn state-k)]
                                     (f player-eid cell)
                                     nil)))))]
       :group/actors [(widget/create
                       {:draw! (fn [this _batch _parent-alpha]
                                 (when-let [stage (.getStage this)]
                                   (let [{:keys [ctx/player-eid
                                                 ctx/ui-mouse-position]
                                          :as ctx} (:stage/ctx stage)]
                                     (draw! ctx
                                            (draw-cell-rect @player-eid
                                                            (.getX this)
                                                            (.getY this)
                                                            (let [[x y] (vector2/->clj (.stageToLocalCoordinates this (vector2/create ui-mouse-position)))]
                                                              (.hit this x y true))
                                                            (.getUserObject (.getParent this)))))))})
                      (image/create
                       {:content background-drawable
                        :actor/name "image-widget"
                        :actor/user-object {:background-drawable background-drawable
                                            :cell-size cell-size}})]})}))
