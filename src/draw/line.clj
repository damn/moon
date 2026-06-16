(ns draw.line
  (:require [space.earlygrey.shape-drawer :as shape-drawer]
            [space.earlygrey.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (shape-drawer/line! shape-drawer sx sy ex ey))
