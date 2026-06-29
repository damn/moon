(ns ctx.explored-tile-corners
  (:require [clojure.grid2d :as g2d])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn step
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (.get (TiledMap/.getProperties tiled-map) "width")
                         (.get (TiledMap/.getProperties tiled-map) "height")
                         (constantly false))))
