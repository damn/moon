(ns ctx.explored-tile-corners
  (:require [tiled-map.get-properties :as get-properties]
            [clojure.grid2d :as g2d])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn step
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (MapProperties/.get (get-properties/f tiled-map) "width")
                         (MapProperties/.get (get-properties/f tiled-map) "height")
                         (constantly false))))
