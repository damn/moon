(ns game.impl.explored-tile-corners
  (:require [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.grid2d :as g2d]))

(defn create
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (props/get (tiled-map/properties tiled-map) "width")
                         (props/get (tiled-map/properties tiled-map) "height")
                         (constantly false))))
