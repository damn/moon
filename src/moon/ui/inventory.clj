(ns moon.ui.inventory
  (:require [moon.ui.image :as image]
            [moon.ui.tooltip :as tooltip])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn- find-cell [group cell]
  (first (filter #(= (Actor/.getUserObject % ) cell)
                 (.getChildren group))))

(defn- window->cell [inventory-window cell]
  (-> inventory-window
      (.findActor "inventory-cell-table")
      (find-cell cell)))

(defn set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (.findActor cell-widget "image-widget")
        cell-size (:cell-size (Actor/.getUserObject image-widget))
        drawable (doto (TextureRegionDrawable. texture-region)
                   (.setMinSize cell-size cell-size))]
    (image/set-drawable! image-widget drawable)
    (tooltip/add! cell-widget tooltip-text skin)))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (.findActor cell-widget "image-widget")]
    (image/set-drawable! image-widget (:background-drawable (Actor/.getUserObject image-widget)))
    (tooltip/remove! cell-widget)))
