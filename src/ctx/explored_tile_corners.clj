(ns ctx.explored-tile-corners
  (:require [map-properties.get :as get]
            [tiled-map.get-properties :as get-properties]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (get/f (get-properties/f tiled-map) "width")
                         (get/f (get-properties/f tiled-map) "height")
                         (constantly false))))
