(ns draw.ellipse
  (:require [clojure.shape-drawer.ellipse :refer [ellipse!]]
            [clojure.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (ellipse! shape-drawer x y radius-x radius-y))
