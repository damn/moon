(ns tiled-map.tile-movement-property
  (:require [map-properties.get :as get]
            [tiled-map-tile.get-properties :as get-properties]
            [tiled-map.get-properties :as tiled-map-get-properties]
            [tiled-map-tile-layer.get-name :refer [get-name]]
            [tiled-map-tile-layer.get-cell :refer [get-cell]]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer-cell :as cell]))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (get-cell layer position)]
      (let [value (-> cell
                      cell/tile
                      get-properties/f
                      (get/f "movement"))]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (get/f (tiled-map-get-properties/f tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (get-name layer) " is undefined."))
        value))))
