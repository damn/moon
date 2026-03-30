(ns moon.modules.load-schema-tiled-map
  (:require [clj.api.com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]))

(defn step [w]
  (assoc w :schema-tiled-map (tmx-map-loader/load! "maps/modules.tmx")))
