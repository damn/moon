(ns clojure.gdx.maps.tiled.tiled-map-tile-layer
  (:require [clojure.tiled-map.layer :as layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(.bindRoot #'layer/create
           (fn [width height tilewidth tileheight]
             (TiledMapTileLayer. width height tilewidth tileheight)))

(extend-type TiledMapTileLayer
  layer/Layer
  (set-visible! [layer visible?]
    (.setVisible layer visible?))

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
