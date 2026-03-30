(ns moon.inventory-window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.actor :as actor])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Skin
                                               TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn- find-cell [group cell]
  (first (filter #(= (actor/user-object %) cell)
                 (group/children group))))

(defn- window->cell [inventory-window cell]
  (-> inventory-window
      (group/find-actor "inventory-cell-table")
      (find-cell cell)))

(defn set-item! [inventory-window cell {:keys [^TextureRegion texture-region tooltip-text]} skin]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (actor/user-object image-widget))
        drawable (doto (TextureRegionDrawable. texture-region)
                   (.setMinSize cell-size cell-size))]
    (Image/.setDrawable image-widget drawable)
    (actor/add-listener! cell-widget (TextTooltip. ^String tooltip-text ^Skin skin))
    nil))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")]
    (Image/.setDrawable image-widget (:background-drawable (actor/user-object image-widget)))
    ; TODO
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
