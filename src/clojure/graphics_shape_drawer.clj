(ns clojure.graphics-shape-drawer
  (:require [clojure.shapedrawer-shape-drawer :as shape-drawer]))

(defn circle! [shape-drawer x y radius]
  (shape-drawer/circle! shape-drawer x y radius))

(defn ellipse! [shape-drawer x y radius-x radius-y]
  (shape-drawer/ellipse! shape-drawer x y radius-x radius-y))

(defn filled-circle! [shape-drawer x y radius]
  (shape-drawer/filled-circle! shape-drawer x y radius))

(defn filled-rectangle! [shape-drawer x y w h]
  (shape-drawer/filled-rectangle! shape-drawer x y w h))

(defn get-default-line-width [shape-drawer]
  (shape-drawer/get-default-line-width shape-drawer))

(defn line! [shape-drawer sx sy ex ey]
  (shape-drawer/line! shape-drawer sx sy ex ey))

(defn new [batch texture-region]
  (shape-drawer/new batch texture-region))

(defn rectangle! [shape-drawer x y w h]
  (shape-drawer/rectangle! shape-drawer x y w h))

(defn sector! [shape-drawer center-x center-y radius start-radians radians]
  (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))

(defn set-color! [shape-drawer color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits))

(defn set-default-line-width! [shape-drawer line-width]
  (shape-drawer/set-default-line-width! shape-drawer line-width))
