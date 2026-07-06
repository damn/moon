(ns moon.ui.inventory-window.create-cell
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.math.vector2 :as vector2]
            [scene2d.ui.widget :as widget]
            [ctx.do :refer [do!]]
            [ctx.draw :refer [draw!]]
            [scene2d.utils.click-listener :as click-listener]
            [scene2d.ui.stack :as stack]))

(defn ->cell [slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (stack/create)]
       (run! #(group/add-actor! stack %)
             [(widget/f
               {:draw! (fn [this _batch _parent-alpha]
                         (when-let [stage (actor/get-stage this)]
                           (let [{:keys [ctx/player-eid
                                         ctx/ui-mouse-position]
                                  :as ctx} (:stage/ctx stage)]
                             (draw! ctx
                                    (draw-cell-rect @player-eid
                                                    (actor/get-x this)
                                                    (actor/get-y this)
                                                    (let [[x y] (vector2/clojurize
                                                                 (actor/stage-to-local-coordinates this
                                                                                              (vector2/new ui-mouse-position)))]
                                                      (actor/hit this x y true))
                                                    (actor/get-user-object (actor/get-parent this)))))))})
              (doto (image/new-drawable background-drawable)
                (actor/set-name! "image-widget")
                (actor/set-user-object! {:background-drawable background-drawable
                                    :cell-size cell-size}))])
       (doto stack
         (actor/add-listener! (click-listener/create
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
         (actor/set-name! "inventory-cell")
         (actor/set-user-object! cell)))}))
