(ns ctx.grid
  (:require [clojure.gdx :as gdx]
            [tiled-map.movement-property :as movement-property]
            [clojure.grid2d :as g2d]
            [moon.records.grid-cell :as grid-cell]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (g2d/create-grid (gdx/map-properties-get (gdx/tiled-map-get-properties tiled-map) "width")
                   (gdx/map-properties-get (gdx/tiled-map-get-properties tiled-map) "height")
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
