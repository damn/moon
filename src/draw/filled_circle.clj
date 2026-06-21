(ns draw.filled-circle
  (:require [moon.shape-drawer.filled-circle :refer [filled-circle!]]
            [moon.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (filled-circle! shape-drawer x y radius))
