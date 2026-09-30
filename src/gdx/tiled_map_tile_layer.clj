(ns gdx.tiled-map-tile-layer
  (:import (com.badlogic.gdx.maps MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMapTile
                                        TiledMapTileLayer
                                        TiledMapTileLayer$Cell)))

(defn property-value [layer [x y] property-key]
  (if-let [cell (.getCell ^TiledMapTileLayer layer (int x) (int y))]
    (if-let [value (.get ^MapProperties (.getProperties ^TiledMapTile (.getTile ^TiledMapTileLayer$Cell cell)) property-key)]
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
  (let [layer (doto (TiledMapTileLayer. (int width) (int height) (int tilewidth) (int tileheight))
                (.setName ^String name)
                (.setVisible visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (.put ^MapProperties (.getProperties ^TiledMapTileLayer layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (.setCell ^TiledMapTileLayer layer (int x) (int y)
                (doto (TiledMapTileLayer$Cell.)
                  (.setTile ^TiledMapTile tile))))
    layer))
