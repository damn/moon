(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer
                                        TiledMapTileLayer$Cell)))

(defn cell [^TiledMapTileLayer layer [x y]]
  (.getCell layer x y))

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
  (let [layer (doto (TiledMapTileLayer. width height tilewidth tileheight)
                (.setName name)
                (.setVisible visible?))]
    (.putAll (.getProperties layer) map-properties)
    (doseq [[xy tiled-map-tile] tiles
            :let [[x y] xy]
            :when tiled-map-tile]
      (.setCell layer x y (doto (TiledMapTileLayer$Cell.)
                            (.setTile tiled-map-tile))))
    layer))

(defn property-value [^TiledMapTileLayer layer [x y] property-key]
  (if-let [cell (.getCell layer x y)]
    (if-let [value (.get (.getProperties (.getTile cell)) property-key)]
      value
      :undefined)
    :no-cell))
