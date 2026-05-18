(ns com.badlogic.gdx.maps.tiled
  (:require [clojure.tiled-map :as tiled-map]
            [clojure.tiled-map.layer :as layer]
            [clojure.tiled-map.layer.cell :as cell]
            [clojure.tiled-map.layers :as layers]
            [clojure.tiled-map.props :as props]
            [clojure.tiled-map.tile :as tile])
  (:import (com.badlogic.gdx.maps MapLayer
                                  MapLayers
                                  MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTile
                                        TiledMapTileLayer
                                        TiledMapTileLayer$Cell
                                        TmxMapLoader)))
(defn- create-layer
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
                (.setName name)
                (.setVisible visible?))]
    (props/add! (layer/properties layer) map-properties)
    (doseq [[pos tile] tiles
            :when tile]
      (layer/set-cell! layer pos (doto (TiledMapTileLayer$Cell.)
                                   (.setTile tile))))
    layer))

(defn load! [tmx-file]
  (.load (TmxMapLoader.) tmx-file))

(extend-type MapLayers
  layers/Layers
  (add! [layers layer]
    (.add layers layer))

  (get [layers ^String layer-name]
    (.get layers layer-name))

  (get-index [layers ^MapLayer layer]
    (.getIndex layers layer)))

(extend-type MapProperties
  props/Props
  (get [map-properties k]
    (.get map-properties k))

  (add! [props m]
    (doseq [[k v] m]
      (assert (string? k))
      (.put props k v)))

  (->clj [props]
    (zipmap (.getKeys props)
            (.getValues props))))

(extend-type TiledMap
  tiled-map/TiledMap
  (properties [this]
    (.getProperties this))

  (layers [this]
    (.getLayers this))

  (add-layer! [tiled-map {:keys [name
                                 visible?
                                 properties
                                 tiles]}]
    (let [props (tiled-map/properties tiled-map) ; shadowing 'properties' otherwise
          layer (create-layer {:width      (props/get props "width")
                               :height     (props/get props "height")
                               :tilewidth  (props/get props "tilewidth")
                               :tileheight (props/get props "tileheight")
                               :name name
                               :visible? visible?
                               :map-properties properties
                               :tiles tiles})]
      (layers/add! (tiled-map/layers tiled-map) layer))
    nil)
  )

(extend-type TiledMapTile
  tile/Tile
  (properties [this]
    (.getProperties this)))

(extend-type TiledMapTileLayer$Cell
  cell/Cell
  (tile [this]
    (.getTile this)))

(extend-type TiledMapTileLayer
  layer/Layer
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
    (.isVisible layer))

  (property-value [layer pos property-key]
    (if-let [cell (layer/cell layer pos)]
      (if-let [value (props/get (tile/properties (cell/tile cell)) property-key)]
        value
        :undefined)
      :no-cell)))

(defn create-map
  [{:keys [properties
           layers]}]
  (let [tiled-map (TiledMap.)]
    (props/add! (tiled-map/properties tiled-map) properties)
    (doseq [layer layers]
      (tiled-map/add-layer! tiled-map layer))
    tiled-map))
