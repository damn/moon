(ns game.impl.explored-tile-corners
  (:require [clojure.maps.tiled.tiled-map :as tiled-map]
            [clojure.maps.map-properties :as props]
            [moon.grid2d :as g2d]))

(defn create
  [tiled-map]
  (atom (g2d/create-grid (props/get (tiled-map/properties tiled-map) "width")
                         (props/get (tiled-map/properties tiled-map) "height")
                         (constantly false))))
