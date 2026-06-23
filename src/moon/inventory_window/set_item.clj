(ns moon.inventory-window.set-item
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.group.find-actor :refer [find-actor]]
            [ui.text-tooltip :as text-tooltip]
            [ui.image :as image]
            [map.texture-region-drawable :as texture-region-drawable]
            [moon.inventory-window.get-cell :as get-cell]))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")
        cell-size (:cell-size (get-user-object image-widget))]
    (image/set-drawable! image-widget (texture-region-drawable/create
                                       {:drawable/texture-region texture-region
                                        :drawable/size cell-size}))
    (add-listener! cell-widget (text-tooltip/create tooltip-text skin))
    nil))
