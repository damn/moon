(ns clojure.draw.rectangle
  (:require [clojure.shape-drawer.rectangle :refer [rectangle!]]
            [clojure.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (rectangle! shape-drawer x y w h))
