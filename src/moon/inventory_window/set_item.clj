(ns moon.inventory-window.set-item
  (:require [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [moon.inventory-window.get-cell :as get-cell]))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (actor/get-user-object image-widget))]
    (image/set-drawable! image-widget (doto (texture-region-drawable/new texture-region)
                                   (texture-region-drawable/set-min-size! cell-size cell-size)))
    (actor/add-listener! cell-widget (text-tooltip/create tooltip-text skin))
    nil))
