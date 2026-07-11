(ns clojure.load-tmx-map
  (:require [com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]))

; moon.tmx-map-loader
; moon.tmx

(defn f [path]
  (tmx-map-loader/load (tmx-map-loader/new) path))
