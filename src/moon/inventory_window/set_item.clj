(ns moon.inventory-window.set-item
  (:require [clojure.gdx :as gdx]
            [scene2d.ui.text-tooltip :as text-tooltip]
            [moon.inventory-window.get-cell :as get-cell]))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (gdx/find-actor cell-widget "image-widget")
        cell-size (:cell-size (gdx/get-user-object image-widget))]
    (gdx/image-set-drawable! image-widget
                              (doto (gdx/texture-region-drawable texture-region)
                                (gdx/texture-region-drawable-set-min-size! cell-size cell-size)))
    (gdx/add-listener! cell-widget (text-tooltip/create tooltip-text skin))
    nil))
