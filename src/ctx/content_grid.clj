(ns ctx.content-grid
  (:require [tiled-map.get-properties :as get-properties]
            [clojure.grid2d :as g2d])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn step
  [{:keys [ctx/tiled-map]}]
  (let [width (MapProperties/.get (get-properties/f tiled-map) "width")
        height (MapProperties/.get (get-properties/f tiled-map) "height")
        cell-size 16]
    {:grid (g2d/create-grid
            (inc (int (/ width  cell-size)))
            (inc (int (/ height cell-size)))
            (fn [idx]
              (atom {:idx idx,
                     :entities #{}})))
     :cell-w cell-size
     :cell-h cell-size}))
