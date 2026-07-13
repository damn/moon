(ns moon.level.tmx
  (:require [gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (tmx-map-loader/load-tiled-map tmx-file)
   :start-position start-position})

(defn vampire [_]
  (create {:tmx-file "maps/vampire.tmx"
           :start-position [32 71]}))
