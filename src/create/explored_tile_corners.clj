(ns create.explored-tile-corners
  (:require [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/explored-tile-corners
         (atom (g2d/create-grid (props/get (tiled-map/props tiled-map) "width")
                                (props/get (tiled-map/props tiled-map) "height")
                                (constantly false)))))
