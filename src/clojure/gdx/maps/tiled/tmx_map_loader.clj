(ns clojure.gdx.maps.tiled.tmx-map-loader
  (:require [com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]))

(defn load-tiled-map [path]
  (tmx-map-loader/load (tmx-map-loader/new) path))
