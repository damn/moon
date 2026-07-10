(ns clojure.moon.create-explored-tile-corners
  (:require [clojure.grid2d :as g2d]
            [gdl.maps.map-properties :as map-properties]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]))

(defn f [ctx]
  (assoc ctx
         :ctx/explored-tile-corners (atom (g2d/create-grid (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "width")
                                                            (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "height")
                                                            (constantly false)))))
