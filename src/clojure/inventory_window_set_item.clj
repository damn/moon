(ns clojure.inventory-window-set-item
  (:require [clojure.texture-region-drawable :as texture-region-drawable]
            [clojure.texture :as texture]
            [clojure.image :as image]
            [clojure.group :as group]
            [clojure.actor :as actor]
            [clojure.ui-text-tooltip :as text-tooltip]
            [clojure.get-cell :as get-cell]))

(defn f [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (get-cell/f inventory-window cell)
        image-widget (group/find-actor cell-widget "image-widget")
        cell-size (:cell-size (actor/get-user-object image-widget))]
    (image/set-drawable! image-widget (doto (texture-region-drawable/new texture-region)
                                   (texture-region-drawable/set-min-size! cell-size cell-size)))
    (actor/add-listener! cell-widget (text-tooltip/create tooltip-text skin))
    nil))
