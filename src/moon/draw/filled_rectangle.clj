(ns moon.draw.filled-rectangle
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clj.api.space.earlygrey.shape-drawer :as sd]))

(defn do!
  [{:keys [ctx/shape-drawer]}
   x y w h color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/filled-rectangle! shape-drawer x y w h))
