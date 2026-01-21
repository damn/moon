(ns moon.draw.rectangle
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   x y w h color]
  (.setColor shape-drawer (color/float-bits color))
  (.rectangle shape-drawer x y w h))
