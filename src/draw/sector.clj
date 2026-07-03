(ns draw.sector
  (:require [clojure.gdx.shape-drawer.sector :as sector]
            [clojure.gdx.shape-drawer.set-color :as set-color]))

(defn f
  [{:keys [ctx/shape-drawer]} [center-x center-y] radius start-radians radians color-float-bits]
  (set-color/f shape-drawer color-float-bits)
  (sector/f shape-drawer center-x center-y radius start-radians radians))
