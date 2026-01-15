(ns moon.draw.ellipse
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clj.api.space.earlygrey.shape-drawer :as sd]))

(defn do!
  [{:keys [ctx/shape-drawer]}
   [x y] radius-x radius-y color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/ellipse! shape-drawer x y radius-x radius-y))
