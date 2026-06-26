(ns tiled.tmx-map-loader
  (:require [com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]))

(defn load! [tmx-file]
  (tmx-map-loader/load! tmx-file))
