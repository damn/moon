(ns clojure.inventory-window-remove-item
  (:require
            [gdl.scenes.scene2d.actor :as actor] [gdl.scenes.scene2d.ui.image :as image]
            [clojure.scene2d.group :as group]
            [clojure.get-cell :as get-cell]))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")]
    (image/set-drawable! image-widget (:background-drawable (actor/get-user-object image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
