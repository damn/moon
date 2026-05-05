(ns space.earlygrey.shapedrawer.shape-drawer
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn create [batch texture-region]
  (ShapeDrawer. batch texture-region))

(defn set-color! [^ShapeDrawer this color-float-bits]
  (.setColor this (float color-float-bits)))

(defn circle! [^ShapeDrawer this x y radius]
  (.circle this x y radius))

(defn ellipse! [^ShapeDrawer this x y radius-x radius-y]
  (.ellipse this x y radius-x radius-y))

(defn filled-circle! [^ShapeDrawer this x y radius]
  (.filledCircle this (float x) (float y) (float radius)))

(defn filled-rectangle! [^ShapeDrawer this x y w h]
  (.filledRectangle this (float x) (float y) (float w) (float h)))

(defn line! [^ShapeDrawer this sx sy ex ey]
  (.line this (float sx) (float sy) (float ex) (float ey)))

(defn rectangle! [^ShapeDrawer this x y w h]
  (.rectangle this x y w h))

(defn sector! [^ShapeDrawer this center-x center-y radius start-radians radians]
  (.sector this center-x center-y radius start-radians radians))

(defn default-line-width [^ShapeDrawer this]
  (.getDefaultLineWidth this))

(defn set-default-line-width! [^ShapeDrawer this width]
  (.setDefaultLineWidth this width))
