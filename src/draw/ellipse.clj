(ns draw.ellipse
  (:require [moon.shape-drawer.ellipse :refer [ellipse!]]
            [moon.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (ellipse! shape-drawer x y radius-x radius-y))
