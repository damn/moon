(ns clojure.draw.circle
  (:require [clojure.shape-drawer.circle :refer [circle!]]
            [clojure.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (circle! shape-drawer x y radius))
