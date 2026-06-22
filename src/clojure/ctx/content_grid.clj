(ns clojure.ctx.content-grid
  (:require [gdl.map-properties.get :refer [props-get]]
            [gdl.get-properties :refer [get-properties]]
            [clojure.grid2d :as g2d]))

(defn step
  [{:keys [ctx/tiled-map]}]
  (let [width (props-get (get-properties tiled-map) "width")
        height (props-get (get-properties tiled-map) "height")
        cell-size 16]
    {:grid (g2d/create-grid
            (inc (int (/ width  cell-size)))
            (inc (int (/ height cell-size)))
            (fn [idx]
              (atom {:idx idx,
                     :entities #{}})))
     :cell-w cell-size
     :cell-h cell-size}))
