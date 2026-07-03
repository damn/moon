(ns draw.filled-rectangle
  (:require [clojure.gdx.shape-drawer.filled-rectangle :as filled-rectangle]
            [clojure.gdx.shape-drawer.set-color :as set-color]))

(defn f
  [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
  (set-color/f shape-drawer color-float-bits)
  (filled-rectangle/f shape-drawer x y w h))
