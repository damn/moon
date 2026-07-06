(ns ctx.grid
  (:require
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.map-properties :as map-properties] [tiled-map.movement-property :as movement-property]
            [clojure.grid2d :as g2d]
            [moon.records.grid-cell :as grid-cell]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (g2d/create-grid (map-properties/get (tiled-map/get-properties tiled-map) "width")
                   (map-properties/get (tiled-map/get-properties tiled-map) "height")
                   (fn [position]
                     (atom
                      (grid-cell/map->R
                       {:position position
                        :middle (mapv (partial + 0.5) position)
                        :movement (case (movement-property/f tiled-map position)
                                    "none" :none
                                    "air"  :air
                                    "all"  :all)
                        :entities #{}
                        :occupied #{}})))))
