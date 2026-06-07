(ns game.ctx.mouse-position
  (:require [com.badlogic.gdx.input :as input]))

(defn mouse-position [{:keys [ctx/input]}]
  (input/position input))
