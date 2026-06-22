(ns clojure.draw.sector
  (:require [gdl.shape-drawer.sector :refer [sector!]]
            [gdl.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (sector! shape-drawer center-x center-y radius start-radians radians))
