(ns clojure.gdx.tiled-map.tmx
  (:require [clj.api.com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]))

(defn load! [path]
  (tmx-map-loader/load! path))
