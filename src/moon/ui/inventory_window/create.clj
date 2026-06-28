(ns moon.ui.inventory-window.create
  (:require [scene2d.actor.set-position :refer [set-position!]]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [scene2d.utils.texture-region-drawable.tint :as tint]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.window :as window]
            [moon.inventory :as inventory]
            [moon.ui.inventory-window.create-cell :refer [->cell]]
            [gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f
  [{:keys [item-rect-color
           droppable-item-color
           not-allowed-drop-item-color
           skin
           position
           slot->texture-region
           cell-size]}]
  (let [slot->drawable (fn [slot]
                         (doto (texture-region-drawable/f (slot->texture-region slot))
                           (set-min-size!/f cell-size cell-size)
                           (tint/f (color/create [1 1 1 0.4]))))
        draw-cell-rect (fn [player-entity x y mouseover? cell]
                         [[:draw/rectangle x y cell-size cell-size item-rect-color]
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory/valid-slot? cell item)
                                          droppable-item-color
                                          not-allowed-drop-item-color)]
                              [:draw/filled-rectangle (inc x) (inc y) (- cell-size 2) (- cell-size 2) color]))])
        ->cell (partial ->cell slot->drawable draw-cell-rect cell-size)]
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
                                    (Actor/.setName "inventory-cell-table"))
                           :pad 4}]]})
      (Actor/.setName "moon.ui.windows.inventory")
      (Actor/.setVisible false)
      (set-position! position))))
