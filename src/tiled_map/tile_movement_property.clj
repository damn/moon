(ns tiled-map.tile-movement-property
  (:import (com.badlogic.gdx.maps MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer$Cell)))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (.getCell layer x y)]
      (let [value (MapProperties/.get (.getProperties (.getTile ^TiledMapTileLayer$Cell cell))
                                      "movement")]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (MapProperties/.get (TiledMap/.getProperties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (.getName layer) " is undefined."))
        value))))
