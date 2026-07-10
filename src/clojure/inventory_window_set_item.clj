(ns clojure.inventory-window-set-item
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [clojure.scene2d.group :as group]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.get-cell :as get-cell]))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (actor/getUserObject image-widget))]
    (image/setDrawable image-widget (doto (texture-region-drawable/new texture-region)
                                   (texture-region-drawable/setMinSize cell-size cell-size)))
    (actor/addListener cell-widget (text-tooltip/create tooltip-text skin))
    nil))
