(ns moon.level.tmx
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (.load (TmxMapLoader.) tmx-file)
   :start-position start-position})

(defn vampire [_]
  (create {:tmx-file "maps/vampire.tmx"
           :start-position [32 71]}))
