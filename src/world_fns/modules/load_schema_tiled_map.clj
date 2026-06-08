(ns world-fns.modules.load-schema-tiled-map
  (:require [com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]))

(defn f [w]
  (assoc w :schema-tiled-map (tmx-map-loader/load! "maps/modules.tmx")))
