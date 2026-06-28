(ns moon.inventory-window.remove-item
  (:require [scene2d.ui.image.set-drawable :as set-drawable!]
            [moon.inventory-window.get-cell :as get-cell])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (Group/.findActor cell-widget "image-widget")]
    (set-drawable!/f image-widget (:background-drawable (Actor/.getUserObject image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
