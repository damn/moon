(ns moon.draw.line
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clj.api.space.earlygrey.shape-drawer :as sd]))

(defn do!
  [{:keys [ctx/shape-drawer]}
   [sx sy] [ex ey] color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/line! shape-drawer sx sy ex ey))
