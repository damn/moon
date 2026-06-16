(ns draw.ellipse
  (:require [space.earlygrey.shape-drawer :as shape-drawer]
            [space.earlygrey.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))
