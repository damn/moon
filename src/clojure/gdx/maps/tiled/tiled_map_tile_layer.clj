(ns clojure.gdx.maps.tiled.tiled-map-tile-layer
  (:require [clojure.tiled-map.layer :as layer]
            [clojure.tiled-map.layer.cell :as cell]
            [clojure.maps.map-properties :as props])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer
                                        TiledMapTileLayer$Cell)))

(.bindRoot #'layer/create
           (fn
             [{:keys [width
                      height
                      tilewidth
                      tileheight
                      name
                      visible?
                      map-properties
                      tiles]}]
             {:pre [(string? name)
                    (boolean? visible?)]}
             (let [layer (doto (TiledMapTileLayer. width height tilewidth tileheight)
                           (layer/set-name! name)
                           (layer/set-visible! visible?))]
               (props/add! (layer/properties layer) map-properties)
               (doseq [[pos tile] tiles
                       :when tile]
                 (layer/set-cell! layer pos (doto (TiledMapTileLayer$Cell.)
                                              (.setTile tile))))
               layer)))

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

(extend-type TiledMapTileLayer$Cell
  cell/Cell
  (tile [this]
    (.getTile this)))
