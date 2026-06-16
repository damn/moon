(ns draw.filled-circle
  (:require [space.earlygrey.shape-drawer.filled-circle :refer [filled-circle!]]
            [space.earlygrey.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (filled-circle! shape-drawer x y radius))
