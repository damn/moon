(ns draw.circle
  (:require [clojure.gdx.shape-drawer.circle :as circle]
            [clojure.gdx.shape-drawer.set-color :as set-color]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (set-color/f shape-drawer color-float-bits)
  (circle/f shape-drawer x y radius))
