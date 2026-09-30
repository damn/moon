(ns gdx.tmx-map-loader
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn load-tiled-map [path]
  (.load (TmxMapLoader.) path))
