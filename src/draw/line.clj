(ns draw.line
  (:require [space.earlygrey.shape-drawer.line :refer [line!]]
            [space.earlygrey.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (line! shape-drawer sx sy ex ey))
