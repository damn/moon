(ns gdl.maps.tiled.tmx
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn load-map
  "Has to be disposed because it loads textures.
  Loads through internal file handle."
  [file-name]
  (.load (TmxMapLoader.) file-name))
