(ns draw.rectangle
  (:require [space.earlygrey.shape-drawer :as shape-drawer]
            [space.earlygrey.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (shape-drawer/rectangle! shape-drawer x y w h))
