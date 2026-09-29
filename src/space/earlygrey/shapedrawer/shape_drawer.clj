(ns space.earlygrey.shapedrawer.shape-drawer
  (:refer-clojure :exclude [new])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn new [batch texture-region]
  (ShapeDrawer. batch texture-region))

(defn circle [^ShapeDrawer shape-drawer x y radius]
  (.circle shape-drawer x y radius))

(defn ellipse [^ShapeDrawer shape-drawer x y radius-x radius-y]
  (.ellipse shape-drawer x y radius-x radius-y))

(defn filledCircle [^ShapeDrawer shape-drawer x y radius]
  (.filledCircle shape-drawer (float x) (float y) (float radius)))

(defn filledRectangle [^ShapeDrawer shape-drawer x y w h]
  (.filledRectangle shape-drawer (float x) (float y) (float w) (float h)))

(defn getDefaultLineWidth [^ShapeDrawer shape-drawer]
  (.getDefaultLineWidth shape-drawer))

(defn line [^ShapeDrawer shape-drawer sx sy ex ey]
  (.line shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn rectangle [^ShapeDrawer shape-drawer x y w h]
  (.rectangle shape-drawer x y w h))

(defn sector [^ShapeDrawer shape-drawer center-x center-y radius start-radians radians]
  (.sector shape-drawer center-x center-y radius start-radians radians))

(defn setColor [^ShapeDrawer shape-drawer color-float-bits]
  (.setColor shape-drawer (float color-float-bits)))

(defn setDefaultLineWidth [^ShapeDrawer shape-drawer line-width]
  (.setDefaultLineWidth shape-drawer line-width))
