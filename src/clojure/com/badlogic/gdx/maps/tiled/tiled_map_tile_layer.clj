(ns clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer
  "@brief Layer for a TiledMap"
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer TiledMapTileLayer$Cell))
  (:refer-clojure :exclude [name]))

(defn create
  "Creates TiledMap layer
  
  - `width`: layer width in tiles
  
  - `height`: layer height in tiles
  
  - `tileWidth`: tile width in pixels
  
  - `tileHeight`: tile height in pixels"
  [width height tilewidth tileheight]
  (TiledMapTileLayer. (int width)
                      (int height)
                      (int tilewidth)
                      (int tileheight)))

(defn properties
  "Returns layer's set of properties"
  [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn width
  "Returns layer's width in tiles"
  [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn height
  "Returns layer's height in tiles"
  [^TiledMapTileLayer layer]
  (.getHeight layer))

(defn name
  "Returns layer's name"
  [^TiledMapTileLayer layer]
  (.getName layer))

(defn set-name!
  "- `name`: new name for the layer"
  [^TiledMapTileLayer layer name]
  (.setName layer name))

(defn visible?
  "Returns whether the layer is visible or not"
  [^TiledMapTileLayer layer]
  (.isVisible layer))

(defn set-visible! [^TiledMapTileLayer layer v]
  (.setVisible layer v))

(defn get-cell
  "- `x`: X coordinate
  
  - `y`: Y coordinate
  
  Returns [[clojure.com.badlogic.gdx.scenes.scene2d.ui.cell]] at (x, y)"
  [^TiledMapTileLayer layer x y]
  (.getCell layer (int x) (int y)))

(defn set-cell!
  "Sets the [[clojure.com.badlogic.gdx.scenes.scene2d.ui.cell]] at the given coordinates.
  
  - `x`: X coordinate
  
  - `y`: Y coordinate
  
  - `cell`: the [[clojure.com.badlogic.gdx.scenes.scene2d.ui.cell]] to set at the given coordinates."
  [^TiledMapTileLayer layer x y ^TiledMapTileLayer$Cell cell]
  (.setCell layer (int x) (int y) cell))

(defn tile-width
  "Returns tiles' width in pixels"
  [^TiledMapTileLayer layer]
  (.getTileWidth layer))

(defn tile-height
  "Returns tiles' height in pixels"
  [^TiledMapTileLayer layer]
  (.getTileHeight layer))

(defn render-offset-x
  "Returns the layer's x render offset, this takes into consideration all parent layers' offsets"
  [^TiledMapTileLayer layer]
  (.getRenderOffsetX layer))

(defn render-offset-y
  "Returns the layer's y render offset, this takes into consideration all parent layers' offsets"
  [^TiledMapTileLayer layer]
  (.getRenderOffsetY layer))
