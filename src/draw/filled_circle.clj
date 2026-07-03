(ns draw.filled-circle
  (:require [clojure.gdx.shape-drawer.filled-circle :as filled-circle]
            [clojure.gdx.shape-drawer.set-color :as set-color]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (set-color/f shape-drawer color-float-bits)
  (filled-circle/f shape-drawer x y radius))
