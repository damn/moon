(ns draw.circle
  (:require [space.earlygrey.shape-drawer.circle :refer [circle!]]
            [space.earlygrey.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (circle! shape-drawer x y radius))
