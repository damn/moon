(ns moon.graphics.draw.rectangle
  (:require [moon.color :as color]
            [gdl.graphics.shape-drawer :as sd]))

(defn do!
  [{:keys [graphics/shape-drawer]}
   x y w h color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/rectangle! shape-drawer x y w h))
