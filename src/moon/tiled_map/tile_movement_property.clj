(ns moon.tiled-map.tile-movement-property
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.map-properties :as map-properties]))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (tiled-map-tile-layer/getCell layer x y)]
      (let [value (map-properties/get (tiled-map-tile/getProperties (tiled-map-tile-layer-cell/getTile cell))
                         "movement")]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (map-properties/get (tiled-map/getProperties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (tiled-map-tile-layer/getName layer) " is undefined."))
        value))))
