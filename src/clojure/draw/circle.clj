(ns clojure.draw.circle
  (:require [gdl.shape-drawer.circle :refer [circle!]]
            [gdl.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (circle! shape-drawer x y radius))
