(ns ctx.content-grid
  (:require [clojure.grid2d :as g2d]
            [clojure.gdx.map-properties.get :as get]
            [clojure.gdx.tiled-map.get-properties :as get-properties]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (let [width  (get/f (get-properties/f tiled-map) "width")
        height (get/f (get-properties/f tiled-map) "height")
        cell-size 16]
    {:grid (g2d/create-grid
            (inc (int (/ width  cell-size)))
            (inc (int (/ height cell-size)))
            (fn [idx]
              (atom {:idx idx,
                     :entities #{}})))
     :cell-w cell-size
     :cell-h cell-size}))
