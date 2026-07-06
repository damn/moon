(ns ctx.draw.circle
  (:require [gdx.graphics.shape-drawer :as shape-drawer]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/circle! shape-drawer x y radius))
