(ns moon.inventory-window
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [clj.api.com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.actor :as actor]))

(defn- find-cell [group cell]
  (first (filter #(= (actor/user-object %) cell)
                 (group/children group))))

(defn- window->cell [inventory-window cell]
  (-> inventory-window
      (group/find-actor "inventory-cell-table")
      (find-cell cell)))

(defn set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (actor/user-object image-widget))
        drawable (doto (texture-region-drawable/create texture-region)
                   (drawable/set-min-size! cell-size cell-size))]
    (image/set-drawable! image-widget drawable)
    (actor/add-listener! cell-widget (text-tooltip/create tooltip-text skin))
    nil))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")]
    (image/set-drawable! image-widget (:background-drawable (actor/user-object image-widget)))
    ; TODO
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
