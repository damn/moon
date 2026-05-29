(ns create.grid
  (:require [moon.grid :as grid]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/grid (grid/create tiled-map)))
