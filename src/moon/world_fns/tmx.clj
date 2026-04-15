(ns moon.world-fns.tmx
  (:require [clojure.gdx.tiled-map.tmx :as tmx-map-loader]))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (tmx-map-loader/load! tmx-file)
   :start-position start-position})
