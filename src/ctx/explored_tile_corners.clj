(ns ctx.explored-tile-corners
  (:require [clojure.gdx :as gdx]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (gdx/map-properties-get (gdx/tiled-map-get-properties tiled-map) "width")
                         (gdx/map-properties-get (gdx/tiled-map-get-properties tiled-map) "height")
                         (constantly false))))
