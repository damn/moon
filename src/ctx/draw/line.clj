(ns ctx.draw.line
  (:require [gdx.graphics.shape-drawer :as shape-drawer]))

(defn f
  [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/line! shape-drawer sx sy ex ey))
