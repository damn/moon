(ns game.world-fns.tmx
  (:require [com.badlogic.gdx.maps.tiled :as tiled]))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (tiled/load! tmx-file)
   :start-position start-position})
