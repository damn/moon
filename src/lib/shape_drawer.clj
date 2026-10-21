(ns lib.shape-drawer
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn create [batch texture-region]
  (ShapeDrawer. batch texture-region))

(defmacro with-line-width [shape-drawer width & body]
  `(let [old-line-width# (space.earlygrey.shapedrawer.ShapeDrawer/.getDefaultLineWidth ~shape-drawer)]
     (space.earlygrey.shapedrawer.ShapeDrawer/.setDefaultLineWidth ~shape-drawer (* ~width old-line-width#))
     ~@body
     (space.earlygrey.shapedrawer.ShapeDrawer/.setDefaultLineWidth ~shape-drawer old-line-width#)))

(defn filled-rectangle! [^ShapeDrawer shape-drawer x y w h color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.filledRectangle shape-drawer (float x) (float y) (float w) (float h)))

(defn rectangle! [^ShapeDrawer shape-drawer x y w h color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.rectangle shape-drawer x y w h))

(defn line! [^ShapeDrawer shape-drawer [sx sy] [ex ey] color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.line shape-drawer (float sx) (float sy) (float ex) (float ey)))

(defn circle! [^ShapeDrawer shape-drawer [x y] radius color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.circle shape-drawer x y radius))

(defn ellipse! [^ShapeDrawer shape-drawer [x y] radius-x radius-y color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.ellipse shape-drawer x y radius-x radius-y))

(defn filled-circle! [^ShapeDrawer shape-drawer [x y] radius color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.filledCircle shape-drawer (float x) (float y) (float radius)))

(defn sector! [^ShapeDrawer shape-drawer [center-x center-y] radius start-radians radians color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.sector shape-drawer center-x center-y radius start-radians radians))
