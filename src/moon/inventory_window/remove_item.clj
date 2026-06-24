(ns moon.inventory-window.remove-item
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.group.find-actor :refer [find-actor]]
            [scene2d.ui.image.set-drawable :as set-drawable!]
            [moon.inventory-window.get-cell :as get-cell]))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")]
    (set-drawable!/f image-widget (:background-drawable (get-user-object image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
