(ns gdx.maps.tiled.tiled-map-tile-layer
  (:require [gdx.maps.map-properties :as map-properties]
            [gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]))

(defn create-layer [width height tilewidth tileheight]
  (tiled-map-tile-layer/new width height tilewidth tileheight))

(defn set-name! [layer name]
  (tiled-map-tile-layer/setName layer name))

(defn set-visible! [layer visible?]
  (tiled-map-tile-layer/setVisible layer visible?))

(defn get-cell [layer x y]
  (tiled-map-tile-layer/getCell layer x y))

(defn set-cell! [layer x y cell]
  (tiled-map-tile-layer/setCell layer x y cell))

(defn get-properties [layer]
  (tiled-map-tile-layer/getProperties layer))

(defn get-name [layer]
  (tiled-map-tile-layer/getName layer))

(defn visible? [layer]
  (tiled-map-tile-layer/isVisible layer))

(defn get-width [layer]
  (tiled-map-tile-layer/getWidth layer))

(defn get-height [layer]
  (tiled-map-tile-layer/getHeight layer))

(defn get-tile-width [layer]
  (tiled-map-tile-layer/getTileWidth layer))

(defn get-tile-height [layer]
  (tiled-map-tile-layer/getTileHeight layer))

(defn get-render-offset-x [layer]
  (tiled-map-tile-layer/getRenderOffsetX layer))

(defn get-render-offset-y [layer]
  (tiled-map-tile-layer/getRenderOffsetY layer))

(defn property-value [layer [x y] property-key]
  (if-let [cell (get-cell layer x y)]
    (if-let [value (map-properties/get (tiled-map-tile/get-properties (cell/get-tile cell)) property-key)]
      value
      :undefined)
    :no-cell))

(defn create
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
  (let [layer (doto (create-layer width height tilewidth tileheight)
                (set-name! name)
                (set-visible! visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (map-properties/put! (get-properties layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (set-cell! layer x y
                 (doto (cell/create)
                   (cell/set-tile! tile))))
    layer))
