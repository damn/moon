(ns moon.inventory-window.remove-item
  (:require [clojure.gdx :as gdx]
            [moon.inventory-window.get-cell :as get-cell])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Image)))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (gdx/find-actor cell-widget "image-widget")]
    (Image/.setDrawable image-widget (:background-drawable (Actor/.getUserObject image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
