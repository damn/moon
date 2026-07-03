(ns tiled-map.tile-movement-property
  (:require [clojure.gdx.map-properties.get :as get]
            [clojure.gdx.tiled-map.get-properties :as get-properties])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (.getCell layer x y)]
      (let [value (get/f (.getProperties (.getTile ^TiledMapTileLayer$Cell cell))
                         "movement")]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (get/f (get-properties/f tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (.getName layer) " is undefined."))
        value))))
