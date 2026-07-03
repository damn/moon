(ns ctx.grid
  (:require [tiled-map.movement-property :as movement-property]
            [clojure.grid2d :as g2d]
            [moon.records.grid-cell :as grid-cell]
            [clojure.gdx.map-properties.get :as get]
            [clojure.gdx.tiled-map.get-properties :as get-properties]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (g2d/create-grid (get/f (get-properties/f tiled-map) "width")
                   (get/f (get-properties/f tiled-map) "height")
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
