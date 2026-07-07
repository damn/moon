(ns clojure.create-cell
  (:require
            [clojure.actor-hit]
            [clojure.add-listener]
            [clojure.get-parent]
            [clojure.get-stage]
            [clojure.get-user-object]
            [clojure.get-x]
            [clojure.get-y]
            [clojure.set-name]
            [clojure.set-user-object]
            [clojure.stage-to-local-coordinates] [clojure.image :as image]
            [clojure.group :as group]
            [clojure.event :as event]
            [clojure.vector2 :as vector2]
            [clojure.ui-widget :as widget]
            [clojure.utils-click-listener :as click-listener]
            [clojure.ui-stack :as stack]
            [clojure.k-clicked-inventory-cell :refer [k->clicked-inventory-cell]]))

(defn ->cell [do! draw! slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
  (let [cell [slot (or position [0 0])]
        background-drawable (slot->drawable slot)]
    {:actor
     (let [stack (stack/create)]
       (run! #(group/add-actor! stack %)
             [(widget/f
               {:draw! (fn [this _batch _parent-alpha]
                         (when-let [stage (clojure.get-stage/f this)]
                           (let [{:keys [ctx/player-eid
                                         ctx/ui-mouse-position]
                                  :as ctx} (:stage/ctx stage)]
                             (draw! ctx
                                    (draw-cell-rect @player-eid
                                                    (clojure.get-x/f this)
                                                    (clojure.get-y/f this)
                                                    (let [[x y] (vector2/clojurize
                                                                 (clojure.stage-to-local-coordinates/f this
                                                                                              (vector2/new ui-mouse-position)))]
                                                      (clojure.actor-hit/f this x y true))
                                                    (clojure.get-user-object/f (clojure.get-parent/f this)))))))})
              (doto (image/new-drawable background-drawable)
                (clojure.set-name/f "image-widget")
                (clojure.set-user-object/f {:background-drawable background-drawable
                                    :cell-size cell-size}))])
       (doto stack
         (clojure.add-listener/f (click-listener/create
                          (fn [event _x _y]
                            (let [{:keys [ctx/player-eid]
                                   :as ctx} (:stage/ctx (event/get-stage event))
                                  entity @player-eid
                                  state-k (:state (:entity/fsm entity))]
                              (do! ctx
                                   (if-let [f (k->clicked-inventory-cell state-k)]
                                     (f player-eid cell)
                                     nil))))))
         (clojure.set-name/f "inventory-cell")
         (clojure.set-user-object/f cell)))}))
