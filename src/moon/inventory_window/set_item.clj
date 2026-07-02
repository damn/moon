(ns moon.inventory-window.set-item
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.get-user-object :as get-user-object]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [moon.inventory-window.get-cell :as get-cell])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (Group/.findActor cell-widget "image-widget")
        cell-size (:cell-size (get-user-object/f image-widget))]
    (Image/.setDrawable image-widget (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                       (.setMinSize cell-size cell-size)))
    (add-listener/f cell-widget (text-tooltip/create tooltip-text skin))
    nil))
