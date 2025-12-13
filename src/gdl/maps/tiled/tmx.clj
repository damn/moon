(ns gdl.maps.tiled.tmx
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn load-map
  "Has to be disposed because it loads textures.
  Loads through internal file handle."
  [file-name]
  (.load (TmxMapLoader.) file-name))

(comment
 ; 1. constructor with: InternalFileHandleResolver

 ; 2. load second arg:
 (com.badlogic.gdx.maps.tiled.BaseTiledMapLoader$Parameters.)

 )
