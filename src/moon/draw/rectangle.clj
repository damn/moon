(ns moon.draw.rectangle
  (:require [clojure.shape-drawer :as shape-drawer]))

(defn do!
  [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/rectangle! shape-drawer x y w h))
