(ns draw.circle
  (:require [shape-drawer.circle :refer [circle!]]
            [gdl.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (circle! shape-drawer x y radius))
