(ns moon.level.tmx
  (:require [moon.tmx :as tmx]))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (tmx/load-tiled-map tmx-file)
   :start-position start-position})

(defn vampire [_]
  (create {:tmx-file "maps/vampire.tmx"
           :start-position [32 71]}))
