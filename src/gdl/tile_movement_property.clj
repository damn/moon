(ns gdl.tile-movement-property
  (:require [map-properties.get :as get]
            [tiled-map-tile.get-properties :as get-properties]
            [tiled-map.get-properties :as tiled-map-get-properties]
            [gdl.tiled-map-tile-layer.get-name :refer [get-name]]
            [gdl.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [gdl.get-tile :as get-tile]))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (get-cell layer position)]
      (let [value (-> cell
                      get-tile/f
                      get-properties/f
                      (get/f "movement"))]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (get/f (tiled-map-get-properties/f tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (get-name layer) " is undefined."))
        value))))
