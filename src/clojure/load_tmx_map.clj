(ns clojure.load-tmx-map
  (:require [gdl.maps.tiled.tmx-map-loader :as tmx-map-loader]))

(defn f [path]
  (tmx-map-loader/load! (tmx-map-loader/new) path))
