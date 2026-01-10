(ns moon.graphics.draw.filled-rectangle
  (:require [moon.color :as color]
            [gdl.graphics.shape-drawer :as sd]))

(defn do!
  [{:keys [graphics/shape-drawer]}
   x y w h color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/filled-rectangle! shape-drawer x y w h))
