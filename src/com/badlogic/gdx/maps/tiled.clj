(ns com.badlogic.gdx.maps.tiled
  (:require [clojure.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
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

(defn- tile-movement-property
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (layer/cell layer position)]
      (let [value (-> cell
                      cell/tile
                      tile/properties
                      (props/get "movement"))]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (props/get (tiled-map/properties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (layer/name layer) " is undefined."))
        value))))

(defn- movement-property-layers
  [tiled-map]
  (->> tiled-map
       tiled-map/layers
       reverse
       (filter #(props/get (layer/properties %) "movement-properties"))))

(defn- movement-properties [tiled-map position]
  (for [layer (movement-property-layers tiled-map)]
    [(layer/name layer)
     (tile-movement-property tiled-map layer position)]))

; out of memory error -> each texture region is a new object
; so either memoize on id or property/image already calculated !? idk
(def ^:private creature-tile
  (memoize
   (fn [{:keys [tile/id
                tile/texture-region]}]
     (assert (and id
                  texture-region))
     (static-tiled-map-tile/create texture-region "id" id))))

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

  (add-creatures-layer! [tiled-map spawn-positions]
    (tiled-map/add-layer! tiled-map
                          {:name "creatures"
                           :visible? false
                           :tiles (for [[position creature-property] spawn-positions]
                                    [position (creature-tile creature-property)])}))

  (spawn-positions [tiled-map]
    (let [layer-name "creatures"
          property-key "id"
          layer (layers/get (tiled-map/layers tiled-map) layer-name)]
      (for [x (range (layer/width layer))
            y (range (layer/height layer))
            :let [position [x y]
                  cell (layer/cell layer position)]
            :when cell
            :let [value (-> cell
                            cell/tile
                            tile/properties
                            (props/get property-key))]
            :when value]
        [position value])))

  (movement-property [tiled-map position]
    (or (->> tiled-map
             movement-property-layers
             (some #(tile-movement-property tiled-map % position)))
        "none"))
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
