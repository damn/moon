(ns gdx.shape-drawer
  (:refer-clojure :exclude [new rectangle])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn new [batch texture-region]
  (ShapeDrawer. batch texture-region))

(defn set-color! [shape-drawer color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits)))

(defn circle [shape-drawer x y radius]
  (.circle ^ShapeDrawer shape-drawer x y radius))

(defn ellipse [shape-drawer x y radius-x radius-y]
  (.ellipse ^ShapeDrawer shape-drawer x y radius-x radius-y))

(defn filled-circle! [shape-drawer x y radius]
  (.filledCircle ^ShapeDrawer shape-drawer (float x) (float y) (float radius)))

(defn filled-rectangle! [shape-drawer x y w h]
  (.filledRectangle ^ShapeDrawer shape-drawer (float x) (float y) (float w) (float h)))

(defn line [shape-drawer sx sy ex ey]
  (.line ^ShapeDrawer shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn rectangle [shape-drawer x y w h]
  (.rectangle ^ShapeDrawer shape-drawer x y w h))

(defn sector [shape-drawer center-x center-y radius start-radians radians]
  (.sector ^ShapeDrawer shape-drawer center-x center-y radius start-radians radians))

(defn get-default-line-width [shape-drawer]
  (.getDefaultLineWidth ^ShapeDrawer shape-drawer))

(defn set-default-line-width! [shape-drawer line-width]
  (.setDefaultLineWidth ^ShapeDrawer shape-drawer line-width))
