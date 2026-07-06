(ns ctx.content-grid
  (:require
            [com.badlogic.gdx.maps.map-properties :as map-properties] [clojure.grid2d :as g2d]
            [clojure.gdx.tiled-map.get-properties :as get-properties]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (let [width  (map-properties/get (get-properties/f tiled-map) "width")
        height (map-properties/get (get-properties/f tiled-map) "height")
        cell-size 16]
    {:grid (g2d/create-grid
            (inc (int (/ width  cell-size)))
            (inc (int (/ height cell-size)))
            (fn [idx]
              (atom {:idx idx,
                     :entities #{}})))
     :cell-w cell-size
     :cell-h cell-size}))
