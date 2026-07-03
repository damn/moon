(ns draw.ellipse
  (:require [clojure.gdx.shape-drawer.ellipse :as ellipse]
            [clojure.gdx.shape-drawer.set-color :as set-color]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
  (set-color/f shape-drawer color-float-bits)
  (ellipse/f shape-drawer x y radius-x radius-y))
