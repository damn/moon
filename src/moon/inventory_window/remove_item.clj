(ns moon.inventory-window.remove-item
  (:require [clojure.actor.get-user-object :refer [get-user-object]]
            [clojure.group.find-actor :refer [find-actor]]
            [clojure.ui.image :as image]
            [moon.inventory-window.get-cell :as get-cell]))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (find-actor cell-widget "image-widget")]
    (image/set-drawable! image-widget (:background-drawable (get-user-object image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
