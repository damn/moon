(ns moon.modules.load-schema-tiled-map
  (:require [clojure.gdx.tiled-map.tmx :as tmx-map-loader]))

(defn step [w]
  (assoc w :schema-tiled-map (tmx-map-loader/load! "maps/modules.tmx")))
