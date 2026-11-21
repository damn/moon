(ns clojure.space.earlygrey.shapedrawer.shape-drawer
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn create [batch texture-region]
  (ShapeDrawer. batch texture-region))

(defn default-line-width [shape-drawer]
  (ShapeDrawer/.getDefaultLineWidth shape-drawer))

(defn set-default-line-width [shape-drawer width]
  (ShapeDrawer/.setDefaultLineWidth shape-drawer width))

(defn set-color! [shape-drawer color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits)))

(defn filled-rectangle! [shape-drawer x y w h]
  (ShapeDrawer/.filledRectangle shape-drawer (float x) (float y) (float w) (float h)))

(defn rectangle! [shape-drawer x y w h]
  (ShapeDrawer/.rectangle shape-drawer x y w h))

(defn line! [shape-drawer sx sy ex ey]
  (ShapeDrawer/.line shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn circle! [shape-drawer x y radius]
  (ShapeDrawer/.circle shape-drawer x y radius))

(defn ellipse! [shape-drawer x y radius-x radius-y]
  (ShapeDrawer/.ellipse shape-drawer x y radius-x radius-y))

(defn filled-circle! [shape-drawer x y radius]
  (ShapeDrawer/.filledCircle shape-drawer (float x) (float y) (float radius)))

(defn sector! [shape-drawer center-x center-y radius start-radians radians]
  (ShapeDrawer/.sector shape-drawer center-x center-y radius start-radians radians))
