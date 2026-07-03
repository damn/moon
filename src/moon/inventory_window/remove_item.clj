(ns moon.inventory-window.remove-item
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.group.find-actor :as find-actor]
            [clojure.gdx.image.set-drawable :as set-drawable]
            [moon.inventory-window.get-cell :as get-cell]))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (find-actor/f cell-widget "image-widget")]
    (set-drawable/f image-widget (:background-drawable (get-user-object/f image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
