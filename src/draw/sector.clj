(ns draw.sector
  (:require [space.earlygrey.shape-drawer.sector :refer [sector!]]
            [space.earlygrey.shape-drawer.set-color :refer [set-color!]]))

(defn f
  [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
  (set-color! shape-drawer color-float-bits)
  (sector! shape-drawer center-x center-y radius start-radians radians))
