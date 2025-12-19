(ns moon.graphics.draw.filled-circle
  (:require [gdl.graphics.color :as color]
            [gdl.graphics.shape-drawer :as sd]))

(defn do!
  [{:keys [graphics/shape-drawer]}
   [x y] radius color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/filled-circle! shape-drawer x y radius))
