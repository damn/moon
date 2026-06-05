(ns clojure.maps.tiled.tmx-map-loader
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn load! [tmx-file] ; TODO clojure.file.load-tmx-map!
  (.load (TmxMapLoader.) tmx-file))
