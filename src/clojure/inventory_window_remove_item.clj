(ns clojure.inventory-window-remove-item
  (:require
            [clojure.scene2d.actor.get-user-object] [clojure.image :as image]
            [clojure.scene2d.group :as group]
            [clojure.get-cell :as get-cell]))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")]
    (image/set-drawable! image-widget (:background-drawable (clojure.scene2d.actor.get-user-object/f image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
