(ns moon.level.tmx
  (:require [clojure.load-tmx-map :as load-tmx-map]))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (load-tmx-map/f tmx-file)
   :start-position start-position})

(defn vampire [_]
  (create {:tmx-file "maps/vampire.tmx"
           :start-position [32 71]}))
