(ns clojure.com.badlogic.gdx.maps.tiled.tmx-map-loader
  "@brief synchronous loader for TMX maps created with the Tiled tool"
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader))
  (:refer-clojure :exclude [load]))

(defn create []
  (TmxMapLoader.))

(defn load
  "Loads the [[clojure.com.badlogic.gdx.maps.tiled.tiled-map]] from the given file. The file is resolved via the file handle resolver set in the
  constructor of this class. By default it will resolve to an internal file. The map will be loaded for a y-up coordinate
  system.
  
  Returns the TiledMap"
  [^TmxMapLoader loader path]
  (.load loader path))
