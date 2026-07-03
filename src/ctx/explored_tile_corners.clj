(ns ctx.explored-tile-corners
  (:require [clojure.grid2d :as g2d]
            [clojure.gdx.map-properties.get :as get]
            [clojure.gdx.tiled-map.get-properties :as get-properties]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (get/f (get-properties/f tiled-map) "width")
                         (get/f (get-properties/f tiled-map) "height")
                         (constantly false))))
