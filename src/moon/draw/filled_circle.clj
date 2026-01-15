(ns moon.draw.filled-circle
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clj.api.space.earlygrey.shape-drawer :as sd]))

(defn do!
  [{:keys [ctx/shape-drawer]}
   [x y] radius color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/filled-circle! shape-drawer x y radius))
