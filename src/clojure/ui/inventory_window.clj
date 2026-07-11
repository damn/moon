(ns clojure.ui.inventory-window
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.ui.inventory-window.cell :refer [->cell]]
            [moon.inventory.cell :as inventory-cell]
            [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

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
                           (texture-region-drawable/setMinSize cell-size cell-size)
                           (texture-region-drawable/tint (color/new [1 1 1 0.4]))))
        draw-cell-rect (fn [player-entity x y mouseover? cell]
                         [[:draw/rectangle x y cell-size cell-size item-rect-color]
                          (when (and mouseover?
                                     (= :player-item-on-cursor (:state (:entity/fsm player-entity))))
                            (let [item (:entity/item-on-cursor player-entity)
                                  color (if (inventory-cell/valid-slot? cell item)
                                          droppable-item-color
                                          not-allowed-drop-item-color)]
                              [:draw/filled-rectangle (inc x) (inc y) (- cell-size 2) (- cell-size 2) color]))])
        ->cell (partial ->cell do! draw! on-click-cell slot->drawable draw-cell-rect cell-size)
        window (doto (doto (window/new "Inventory" skin)
                     (table-set-opts/set-opts!
                      {:table/rows [[{:actor (doto (doto (table/new)
                                                  (table-set-opts/set-opts!
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
                                                                            (->cell :inventory.slot/bag :position [x y]))))}))
                                                (actor/setName "inventory-cell-table"))
                                     :pad 4}]]}))
                (actor/setName "moon.ui.windows.inventory")
                (actor/setVisible false))]
    (let [[x y] position]
      (actor/setPosition window x y))
    window))
