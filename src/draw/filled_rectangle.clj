(ns draw.filled-rectangle
  (:require [shape-drawer.filled-rectangle :refer [filled-rectangle!]]
            [shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (filled-rectangle! shape-drawer x y w h))
