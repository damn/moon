(ns ctx.content-grid
  (:require [clojure.gdx :as gdx]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (let [width  (gdx/map-properties-get (gdx/tiled-map-get-properties tiled-map) "width")
        height (gdx/map-properties-get (gdx/tiled-map-get-properties tiled-map) "height")
        cell-size 16]
    {:grid (g2d/create-grid
            (inc (int (/ width  cell-size)))
            (inc (int (/ height cell-size)))
            (fn [idx]
              (atom {:idx idx,
                     :entities #{}})))
     :cell-w cell-size
     :cell-h cell-size}))
