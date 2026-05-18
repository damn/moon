(ns com.badlogic.gdx.maps.tiled
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn load! [tmx-file]
  (.load (TmxMapLoader.) tmx-file))
