(ns moon.inventory-window.set-item
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.group.find-actor :as find-actor]
            [clojure.gdx.image.set-drawable :as set-drawable]
            [clojure.gdx.texture-region-drawable.new :as new-texture-region-drawable]
            [clojure.gdx.texture-region-drawable.set-min-size :as set-min-size]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [moon.inventory-window.get-cell :as get-cell]))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (find-actor/f cell-widget "image-widget")
        cell-size (:cell-size (get-user-object/f image-widget))]
    (set-drawable/f image-widget (doto (new-texture-region-drawable/f texture-region)
                                   (set-min-size/f cell-size cell-size)))
    (add-listener/f cell-widget (text-tooltip/create tooltip-text skin))
    nil))
