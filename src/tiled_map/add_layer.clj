(ns tiled-map.add-layer
  (:require [tiled-map.create-layer :as create-layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f [^TiledMap tiled-map layer]
  (.add (.getLayers tiled-map)
        (create-layer/f tiled-map layer)))
