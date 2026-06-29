(ns ctx.content-grid
  (:require [clojure.grid2d :as g2d])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn step
  [{:keys [ctx/tiled-map]}]
  (let [width  (.get (TiledMap/.getProperties tiled-map) "width")
        height (.get (TiledMap/.getProperties tiled-map) "height")
        cell-size 16]
    {:grid (g2d/create-grid
            (inc (int (/ width  cell-size)))
            (inc (int (/ height cell-size)))
            (fn [idx]
              (atom {:idx idx,
                     :entities #{}})))
     :cell-w cell-size
     :cell-h cell-size}))
