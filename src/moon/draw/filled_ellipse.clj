(ns moon.draw.filled-ellipse
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   [x y] radius-x radius-y color]
  (.setColor shape-drawer (color/float-bits color))
  (.filledEllipse shape-drawer x y radius-x radius-y))
