(ns moon.inventory-window.set-item
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.group.find-actor :refer [find-actor]]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [scene2d.ui.image.set-drawable :as set-drawable!]
            [scene2d.utils.drawable.set-min-size :as set-min-size!]
            [scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.inventory-window.get-cell :as get-cell]))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")
        cell-size (:cell-size (get-user-object image-widget))]
    (set-drawable!/f image-widget (doto (texture-region-drawable/f texture-region)
                                    (set-min-size!/f cell-size cell-size)))
    (add-listener! cell-widget (text-tooltip/create tooltip-text skin))
    nil))
