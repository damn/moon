(ns clojure.gdx.maps.tiled.tiled-map-tile-layer
  (:require [clojure.maps.tiled.tiled-map-tile-layer :as layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(.bindRoot #'layer/create
           (fn [width height tilewidth tileheight]
             (TiledMapTileLayer. width height tilewidth tileheight)))

(extend-type TiledMapTileLayer
  layer/Layer
  (set-visible! [layer visible?]
    (.setVisible layer visible?))

  (set-name! [layer name]
    (.setName layer name))

  (properties [layer]
    (.getProperties layer))

  (name [layer]
    (.getName layer))

  (cell [layer [x y]]
    (.getCell layer x y))

  (set-cell! [layer [x y] cell]
    (.setCell layer x y cell))

  (width [layer]
    (.getWidth layer))

  (height [layer]
    (.getHeight layer))

  (visible? [layer]
    (.isVisible layer)))
