(ns moon.inventory-window.set-item
  (:require [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.ui.image.set-drawable :as set-drawable!]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.inventory-window.get-cell :as get-cell])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (Group/.findActor cell-widget "image-widget")
        cell-size (:cell-size (Actor/.getUserObject image-widget))]
    (set-drawable!/f image-widget (doto (texture-region-drawable/f texture-region)
                                    (set-min-size!/f cell-size cell-size)))
    (Actor/.addListener cell-widget (text-tooltip/create tooltip-text skin))
    nil))
