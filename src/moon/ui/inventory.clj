(ns moon.ui.inventory
  (:require [moon.scene2d.actor :as actor]
            [moon.scene2d.group :as group]
            [moon.scene2d.ui.image :as image]
            [moon.scene2d.utils.drawable :as drawable]
            [moon.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.ui.tooltip :as tooltip]))

(defn- find-cell [group cell]
  (first (filter #(= (actor/user-object % ) cell)
                 (group/children group))))

(defn- window->cell [inventory-window cell]
  (-> inventory-window
      (group/find-actor "inventory-cell-table")
      (find-cell cell)))

(defn set-item! [inventory-window cell {:keys [texture-region tooltip-text]}]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (actor/user-object image-widget))
        drawable (doto (texture-region-drawable/create texture-region)
                   (drawable/set-min-size! cell-size cell-size))]
    (image/set-drawable! image-widget drawable)
    (tooltip/add! cell-widget tooltip-text)))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")]
    (image/set-drawable! image-widget (:background-drawable (actor/user-object image-widget)))
    (tooltip/remove! cell-widget)))
