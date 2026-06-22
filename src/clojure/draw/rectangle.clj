(ns clojure.draw.rectangle
  (:require [gdl.shape-drawer.rectangle :refer [rectangle!]]
            [gdl.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (rectangle! shape-drawer x y w h))
