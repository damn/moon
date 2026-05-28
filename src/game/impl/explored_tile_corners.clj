(ns game.impl.explored-tile-corners
  (:require [moon.grid2d :as g2d]))

(defn create
  [tiled-map]
  (atom (g2d/create-grid (.get (.getProperties tiled-map) "width")
                         (.get (.getProperties tiled-map) "height")
                         (constantly false))))
