(ns moon.inventory-window.remove-item
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [moon.inventory-window.get-cell :as get-cell])
  (:import (com.badlogic.gdx.scenes.scene2d Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image)))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (Group/.findActor cell-widget "image-widget")]
    (Image/.setDrawable image-widget (:background-drawable (get-user-object/f image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
