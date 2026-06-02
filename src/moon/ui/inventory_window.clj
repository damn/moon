(ns moon.ui.inventory-window
  (:require [clojure.gdx.scene2d.group.find-actor :refer [find-actor]]
            [clojure.gdx.scene2d.group.children :refer [children]]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]
            [gdx.scenes.scene2d.ui.image :as image]
            [gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn- find-inventory-window-cell [group cell]
  (first (filter #(= (.getUserObject %) cell)
                 (children group))))

(defn- window->cell [inventory-window cell]
  (-> inventory-window
      (find-actor "inventory-cell-table")
      (find-inventory-window-cell cell)))

(defn set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")
        cell-size (:cell-size (.getUserObject image-widget))]
    (image/set-drawable! image-widget (texture-region-drawable/create
                                       {:drawable/texture-region texture-region
                                        :drawable/size cell-size}))
    (.addListener cell-widget (text-tooltip/create tooltip-text skin))
    nil))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")]
    (image/set-drawable! image-widget (:background-drawable (.getUserObject image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
