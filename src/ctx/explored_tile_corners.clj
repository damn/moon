(ns ctx.explored-tile-corners
  (:require
            [com.badlogic.gdx.maps.map-properties :as map-properties] [clojure.grid2d :as g2d]
            [clojure.gdx.tiled-map.get-properties :as get-properties]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (map-properties/get (get-properties/f tiled-map) "width")
                         (map-properties/get (get-properties/f tiled-map) "height")
                         (constantly false))))
