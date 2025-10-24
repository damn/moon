(ns clojure.gdx.graphics.g2d.shape-drawer
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn create [batch texture-region]
  (ShapeDrawer. batch texture-region))

(defn default-line-width [^ShapeDrawer this]
  (.getDefaultLineWidth this))

(defn set-default-line-width! [^ShapeDrawer this width]
  (.setDefaultLineWidth this width))

(defmacro with-line-width [this width & exprs]
  `(let [old-line-width# (default-line-width ~this)]
     (set-default-line-width! ~this (* ~width old-line-width#))
     ~@exprs
     (set-default-line-width! ~this old-line-width#)))

(defn set-color! [^ShapeDrawer this float-bits]
  (.setColor this (float float-bits)))

(defn arc! [^ShapeDrawer this center-x center-y radius start-angle radians]
  (.arc this center-x center-y radius start-angle radians))

(defn circle! [^ShapeDrawer this x y radius]
  (.circle this x y radius))

(defn ellipse! [^ShapeDrawer this x y radius-x radius-y]
  (.ellipse this x y radius-x radius-y))

(defn filled-ellipse! [^ShapeDrawer this x y radius-x radius-y]
  (.filledEllipse this x y radius-x radius-y))

(defn filled-circle! [^ShapeDrawer this x y radius]
  (.filledCircle this (float x) (float y) (float radius)))

(defn filled-rectangle! [^ShapeDrawer this x y w h]
  (.filledRectangle this (float x) (float y) (float w) (float h)))

(defn line! [^ShapeDrawer this sx sy ex ey]
  (.line this (float sx) (float sy) (float ex) (float ey)))

(defn rectangle! [^ShapeDrawer this x y w h]
  (.rectangle this x y w h))

(defn sector! [^ShapeDrawer this center-x center-y radius start-angle radians]
  (.sector this center-x center-y radius start-angle radians))
