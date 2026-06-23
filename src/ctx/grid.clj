(ns ctx.grid
  (:require [map-properties.get :as get]
            [tiled-map.get-properties :as get-properties]
            [gdl.movement-property :as movement-property]
            [moon.cell :as cell]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (g2d/create-grid (get/f (get-properties/f tiled-map) "width")
                   (get/f (get-properties/f tiled-map) "height")
                   (fn [position]
                     (atom (cell/create position
                                        (case (movement-property/f tiled-map position)
                                          "none" :none
                                          "air"  :air
                                          "all"  :all))))))
