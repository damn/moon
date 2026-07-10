(ns clojure.moon.create-content-grid
  (:require [clojure.grid2d :as g2d]
            [gdl.maps.map-properties :as map-properties]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]))

(defn f [ctx]
  (let [width (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "width")
        height (map-properties/get (tiled-map/getProperties (:ctx/tiled-map ctx)) "height")
        cell-size 16]
    (assoc ctx
           :ctx/content-grid {:grid (g2d/create-grid
                                     (inc (int (/ width cell-size)))
                                     (inc (int (/ height cell-size)))
                                     (fn [idx]
                                       (atom {:idx idx
                                              :entities #{}})))
                              :cell-w cell-size
                              :cell-h cell-size})))
