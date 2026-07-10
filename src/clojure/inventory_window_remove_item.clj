(ns clojure.inventory-window-remove-item
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.scenes.scene2d.ui.image :as image]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [clojure.get-cell :as get-cell]))

(defn f [inventory-window cell]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (group/findActor cell-widget "image-widget")]
    (image/setDrawable image-widget (:background-drawable (actor/getUserObject image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
