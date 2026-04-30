(ns moon.world-fns.tmx
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (.load (TmxMapLoader.) tmx-file)
   :start-position start-position})
