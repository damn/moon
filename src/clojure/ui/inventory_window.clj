(ns clojure.ui.inventory-window
  (:require [gdl.scenes.scene2d.actor :as actor]
            [clojure.scene2d.actor.set-position! :as actor-set-position]
            [clojure.ui.inventory-window.cell :refer [->cell]]
            [clojure.is-valid-slot :as valid-slot?]
            [clojure.new-color]
            [gdl.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [clojure.ui-table :as table]
            [clojure.ui-window :as window]))

(defn inventory-window-build
  [{:keys [do!
           draw!
           on-click-cell
           item-rect-color
           droppable-item-color
           not-allowed-drop-item-color
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (texture-region-drawable/new (slot->texture-region slot))
                           (texture-region-drawable/set-min-size! cell-size cell-size)
                           (texture-region-drawable/tint! (clojure.new-color/f [1 1 1 0.4]))))
        draw-cell-rect (fn [player-entity x y mouseover? cell]
                         [[:draw/rectangle x y cell-size cell-size item-rect-color]
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (valid-slot?/f cell item)
                                          droppable-item-color
                                          not-allowed-drop-item-color)]
                              [:draw/filled-rectangle (inc x) (inc y) (- cell-size 2) (- cell-size 2) color]))])
        ->cell (partial ->cell do! draw! on-click-cell slot->drawable draw-cell-rect cell-size)]
    (doto (window/create
           {:title "Inventory"
            :skin skin
            :table/rows [[{:actor (doto (table/create
                                         {:table/rows (concat [[nil nil
                                                                (->cell :inventory.slot/helm)
                                                                (->cell :inventory.slot/necklace)]
                                                               [nil
                                                                (->cell :inventory.slot/weapon)
                                                                (->cell :inventory.slot/chest)
                                                                (->cell :inventory.slot/cloak)
                                                                (->cell :inventory.slot/shield)]
                                                               [nil nil
                                                                (->cell :inventory.slot/leg)]
                                                               [nil
                                                                (->cell :inventory.slot/glove)
                                                                (->cell :inventory.slot/rings :position [0 0])
                                                                (->cell :inventory.slot/rings :position [1 0])
                                                                (->cell :inventory.slot/boot)]]
                                                              (for [y (range 4)]
                                                                (for [x (range 6)]
                                                                  (->cell :inventory.slot/bag :position [x y]))))})
                                    (actor/set-name "inventory-cell-table"))
                           :pad 4}]]})
      (actor/set-name "moon.ui.windows.inventory")
      (actor/set-visible false)
      (actor-set-position/set-position! position))))
