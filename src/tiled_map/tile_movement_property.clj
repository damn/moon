(ns tiled-map.tile-movement-property
  (:require [tiled-map-tile-layer.get-name :refer [get-name]]
            [tiled-map-tile-layer.get-cell :refer [get-cell]])
  (:import (com.badlogic.gdx.maps MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer$Cell)))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (get-cell layer position)]
      (let [value (MapProperties/.get (.getProperties (.getTile ^TiledMapTileLayer$Cell cell))
                                      "movement")]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (MapProperties/.get (TiledMap/.getProperties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (get-name layer) " is undefined."))
        value))))
