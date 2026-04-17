(ns moon.world-fns.tmx
  (:require [clojure.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (tmx-map-loader/load! tmx-file)
   :start-position start-position})
