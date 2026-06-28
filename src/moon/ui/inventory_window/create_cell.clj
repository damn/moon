(ns moon.ui.inventory-window.create-cell
  (:require [gdx.math.vector2.clojurize :as clojurize]
            [scene2d.ui.widget :as widget]
            [ctx.do :refer [do!]]
            [ctx.draw :refer [draw!]]
            [game.state :as state]
            [scene2d.utils.click-listener :as click-listener]
            [scene2d.ui.image :as image]
            [scene2d.ui.stack :as stack]
            [gdx.math.vector2 :as vector2])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Event Group)))

(defn ->cell [slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (stack/create)]
       (run! #(Group/.addActor stack %)
             [(widget/f
               {:draw! (fn [this _batch _parent-alpha]
                         (when-let [stage (Actor/.getStage this)]
                           (let [{:keys [ctx/player-eid
                                         ctx/ui-mouse-position]
                                  :as ctx} (:stage/ctx stage)]
                             (draw! ctx
                                    (draw-cell-rect @player-eid
                                                    (Actor/.getX this)
                                                    (Actor/.getY this)
                                                    (Actor/.hit this
                                                                (clojurize/f
                                                                 (Actor/.stageToLocalCoordinates this
                                                                                                 (vector2/f ui-mouse-position)))
                                                                true)
                                                    (Actor/.getUserObject (Actor/.getParent this)))))))})
              (doto (image/create background-drawable)
                (Actor/.setName "image-widget")
                (Actor/.setUserObject {:background-drawable background-drawable
                                       :cell-size cell-size}))])
       (doto stack
         (Actor/.addListener (click-listener/create
                              (fn [event _x _y]
                                (let [{:keys [ctx/player-eid
                                              ctx/k->clicked-inventory-cell]
                                       :as ctx} (:stage/ctx (Event/.getStage event))
                                      entity @player-eid
                                      state-k (:state (:entity/fsm entity))]
                                  (do! ctx
                                       (if-let [f (k->clicked-inventory-cell state-k)]
                                         (f player-eid cell)
                                         nil))))))
         (Actor/.setName "inventory-cell")
         (Actor/.setUserObject cell)))}))
