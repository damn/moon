(ns draw.line
  (:require [moon.shape-drawer.line :refer [line!]]
            [moon.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (line! shape-drawer sx sy ex ey))
