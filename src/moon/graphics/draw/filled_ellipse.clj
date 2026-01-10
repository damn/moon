(ns moon.graphics.draw.filled-ellipse
  (:require [moon.color :as color]
            [gdl.graphics.shape-drawer :as sd]))

(defn do!
  [{:keys [graphics/shape-drawer]}
   [x y] radius-x radius-y color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/filled-ellipse! shape-drawer x y radius-x radius-y))
