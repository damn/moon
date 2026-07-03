(ns draw.line
  (:require [clojure.gdx.shape-drawer.line :as line]
            [clojure.gdx.shape-drawer.set-color :as set-color]))

(defn f
  [{:keys [ctx/shape-drawer]} [sx sy] [ex ey] color-float-bits]
  (set-color/f shape-drawer color-float-bits)
  (line/f shape-drawer sx sy ex ey))
