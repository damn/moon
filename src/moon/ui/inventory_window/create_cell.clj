(ns moon.ui.inventory-window.create-cell
  (:require [clojure.gdx :as gdx]
            [gdx.math.vector2.clojurize :as clojurize]
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
       (run! #(gdx/add-actor! stack %)
             [(widget/f
               {:draw! (fn [this _batch _parent-alpha]
                         (when-let [stage (gdx/get-stage this)]
                           (let [{:keys [ctx/player-eid
                                         ctx/ui-mouse-position]
                                  :as ctx} (:stage/ctx stage)]
                             (draw! ctx
                                    (draw-cell-rect @player-eid
                                                    (gdx/get-x this)
                                                    (gdx/get-y this)
                                                    (let [[x y] (clojurize/f
                                                                 (gdx/stage-to-local-coordinates this
                                                                                                 (gdx/vector2 ui-mouse-position)))]
                                                      (gdx/actor-hit this x y true))
                                                    (gdx/get-user-object (gdx/get-parent this)))))))})
              (doto (gdx/image background-drawable)
                (gdx/image-set-name! "image-widget")
                (gdx/image-set-user-object! {:background-drawable background-drawable
                                             :cell-size cell-size}))])
       (doto stack
         (gdx/add-listener! (click-listener/create
                             (fn [event _x _y]
                               (let [{:keys [ctx/player-eid
                                             ctx/k->clicked-inventory-cell]
                                      :as ctx} (:stage/ctx (gdx/event-get-stage event))
                                     entity @player-eid
                                     state-k (:state (:entity/fsm entity))]
                                 (do! ctx
                                      (if-let [f (k->clicked-inventory-cell state-k)]
                                        (f player-eid cell)
                                        nil))))))
         (gdx/set-name! "inventory-cell")
         (gdx/set-user-object! cell)))}))
