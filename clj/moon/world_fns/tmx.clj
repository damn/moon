(ns moon.world-fns.tmx
  (:require [gdl.maps.tiled.tmx :as tmx]))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (tmx/load-map tmx-file)
   :start-position start-position})
