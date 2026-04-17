(ns moon.draw.sector
  (:require [clojure.graphics.shape-drawer :as shape-drawer]))

(defn do!
  [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
  (shape-drawer/set-color! shape-drawer color-float-bits)
  (shape-drawer/sector! shape-drawer center-x center-y radius start-radians radians))
