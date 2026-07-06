(ns ctx.draw.rectangle
  (:require [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer]))

(defn f
  [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/rectangle! shape-drawer x y w h))
