(ns moon.graphics.draw.line
  (:require [moon.color :as color]
            [gdl.graphics.shape-drawer :as sd]))

(defn do!
  [{:keys [graphics/shape-drawer]}
   [sx sy] [ex ey] color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/line! shape-drawer sx sy ex ey))
