(ns moon.inventory-window
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn- get-cell [inventory-window cell]
  (->> "inventory-cell-table"
       (#(group/findActor inventory-window %))
       group/getChildren
       (filter #(= (actor/getUserObject %) cell))
       first))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (get-cell inventory-window cell)
        image-widget (group/findActor cell-widget "image-widget")]
    (image/setDrawable image-widget (:background-drawable (actor/getUserObject image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell inventory-window cell)
        image-widget (group/findActor cell-widget "image-widget")
        cell-size (:cell-size (actor/getUserObject image-widget))]
    (image/setDrawable image-widget (doto (texture-region-drawable/new texture-region)
                                        (texture-region-drawable/setMinSize cell-size cell-size)))
    (actor/addListener cell-widget (text-tooltip/new tooltip-text skin))
    nil))
