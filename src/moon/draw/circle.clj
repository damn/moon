(ns moon.draw.circle
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   [x y] radius color]
  (.setColor shape-drawer (color/float-bits color))
  (.circle shape-drawer x y radius))
