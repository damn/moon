(ns moon.ui.inventory-window.create-cell
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.get-parent :as get-parent]
            [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.actor.get-stage :as get-stage]
            [clojure.gdx.actor.get-x :as get-x]
            [clojure.gdx.actor.get-y :as get-y]
            [clojure.gdx.actor.hit :as hit]
            [clojure.gdx.actor.set-name :as set-name]
            [clojure.gdx.actor.set-user-object :as set-user-object]
            [clojure.gdx.actor.stage-to-local-coordinates :as stage-to-local-coordinates]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.group.add-actor :as add-actor]
            [clojure.gdx.image.new-drawable :as new-image]
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
       (run! #(add-actor/f stack %)
             [(widget/f
               {:draw! (fn [this _batch _parent-alpha]
                         (when-let [stage (get-stage/f this)]
                           (let [{:keys [ctx/player-eid
                                         ctx/ui-mouse-position]
                                  :as ctx} (:stage/ctx stage)]
                             (draw! ctx
                                    (draw-cell-rect @player-eid
                                                    (get-x/f this)
                                                    (get-y/f this)
                                                    (let [[x y] (clojurize/f
                                                                 (stage-to-local-coordinates/f this
                                                                                              (new-vector2/f ui-mouse-position)))]
                                                      (hit/f this x y true))
                                                    (get-user-object/f (get-parent/f this)))))))})
              (doto (new-image/f background-drawable)
                (set-name/f "image-widget")
                (set-user-object/f {:background-drawable background-drawable
                                    :cell-size cell-size}))])
       (doto stack
         (add-listener/f (click-listener/create
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
         (set-name/f "inventory-cell")
         (set-user-object/f cell)))}))
