(ns ctx.explored-tile-corners
  (:require [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (map-properties/get (tiled-map/get-properties tiled-map) "width")
                         (map-properties/get (tiled-map/get-properties tiled-map) "height")
                         (constantly false))))
