(ns moon.ui.inventory
  (:require [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Skin
                                               TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn- find-cell [group cell]
  (first (filter #(= (Actor/.getUserObject %) cell)
                 (group/children group))))

(defn- window->cell [inventory-window cell]
  (-> inventory-window
      (group/find-actor "inventory-cell-table")
      (find-cell cell)))

(defn set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (Actor/.getUserObject image-widget))
        drawable (doto (TextureRegionDrawable. texture-region)
                   (.setMinSize cell-size cell-size))]
    (Image/.setDrawable image-widget drawable)
    (.addListener cell-widget (TextTooltip. tooltip-text ^Skin skin))
    nil))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")]
    (Image/.setDrawable image-widget (:background-drawable (Actor/.getUserObject image-widget)))
    ; TODO
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
