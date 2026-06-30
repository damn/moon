(ns moon.inventory-window.remove-item
  (:require [clojure.gdx :as gdx]
            [moon.inventory-window.get-cell :as get-cell]))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (gdx/find-actor cell-widget "image-widget")]
    (gdx/image-set-drawable! image-widget (:background-drawable (gdx/get-user-object image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
