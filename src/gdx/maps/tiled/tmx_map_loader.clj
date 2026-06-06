(ns gdx.maps.tiled.tmx-map-loader
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn load! [tmx-file]
  (.load (TmxMapLoader.) tmx-file))
