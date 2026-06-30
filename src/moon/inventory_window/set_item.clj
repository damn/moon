(ns moon.inventory-window.set-item
  (:require [clojure.gdx :as gdx]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [moon.inventory-window.get-cell :as get-cell])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (gdx/find-actor cell-widget "image-widget")
        cell-size (:cell-size (Actor/.getUserObject image-widget))]
    (Image/.setDrawable image-widget (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                       (.setMinSize cell-size cell-size)))
    (Actor/.addListener cell-widget (text-tooltip/create tooltip-text skin))
    nil))
