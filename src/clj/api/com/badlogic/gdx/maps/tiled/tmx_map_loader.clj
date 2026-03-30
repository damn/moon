(ns clj.api.com.badlogic.gdx.maps.tiled.tmx-map-loader
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn load! [path]
  (.load (TmxMapLoader.) path))
