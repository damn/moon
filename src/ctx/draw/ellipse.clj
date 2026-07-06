(ns ctx.draw.ellipse
  (:require [gdx.graphics.shape-drawer :as shape-drawer]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))
