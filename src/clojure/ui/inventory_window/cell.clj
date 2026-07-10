(ns clojure.ui.inventory-window.cell
  (:require
            [gdl.actor :as actor] [gdl.image :as image]
            [clojure.scene2d.group :as group]
            [gdl.event :as event]
            [gdl.vector2 :as vector2]
            [clojure.ui-widget :as widget]
            [clojure.scene2d.utils.click-listener :as click-listener]
            [clojure.ui-stack :as stack]))

(defn ->cell [do! draw! on-click-cell slot->drawable draw-cell-rect cell-size slot & {:keys [position]}]
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
                                                    (let [[mx my] ui-mouse-position
                                                          [x y] (vector2/clojurize
                                                                 (actor/stage-to-local-coordinates this
                                                                                              (vector2/new mx my)))]
                                                      (actor/hit this x y true))
                                                    (actor/get-user-object (actor/get-parent this)))))))})
              (doto (image/new-drawable background-drawable)
                (actor/set-name "image-widget")
                (actor/set-user-object {:background-drawable background-drawable
                                    :cell-size cell-size}))])
       (doto stack
         (actor/add-listener (click-listener/create
                          (fn [event _x _y]
                            (let [{:keys [ctx/player-eid]
                                   :as ctx} (:stage/ctx (event/get-stage event))]
                              (do! ctx (on-click-cell player-eid cell))))))
         (actor/set-name "inventory-cell")
         (actor/set-user-object cell)))}))
