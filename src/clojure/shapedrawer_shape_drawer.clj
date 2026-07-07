(ns clojure.shapedrawer-shape-drawer
  (:refer-clojure :exclude [new])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn circle! [^ShapeDrawer shape-drawer x y radius]
  (ShapeDrawer/.circle shape-drawer x y radius))

(defn ellipse! [^ShapeDrawer shape-drawer x y radius-x radius-y]
  (ShapeDrawer/.ellipse shape-drawer x y radius-x radius-y))

(defn filled-circle! [^ShapeDrawer shape-drawer x y radius]
  (ShapeDrawer/.filledCircle shape-drawer (float x) (float y) (float radius)))

(defn filled-rectangle! [^ShapeDrawer shape-drawer x y w h]
  (ShapeDrawer/.filledRectangle shape-drawer (float x) (float y) (float w) (float h)))

(defn get-default-line-width [^ShapeDrawer shape-drawer]
  (ShapeDrawer/.getDefaultLineWidth shape-drawer))

(defn line! [^ShapeDrawer shape-drawer sx sy ex ey]
  (ShapeDrawer/.line shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn new [batch texture-region]
  (ShapeDrawer. batch texture-region))

(defn rectangle! [^ShapeDrawer shape-drawer x y w h]
  (ShapeDrawer/.rectangle shape-drawer x y w h))

(defn sector! [^ShapeDrawer shape-drawer center-x center-y radius start-radians radians]
  (ShapeDrawer/.sector shape-drawer center-x center-y radius start-radians radians))

(defn set-color! [^ShapeDrawer shape-drawer color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits)))

(defn set-default-line-width! [^ShapeDrawer shape-drawer line-width]
  (ShapeDrawer/.setDefaultLineWidth shape-drawer line-width))
