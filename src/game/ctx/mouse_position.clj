(ns game.ctx.mouse-position
  (:require [com.badlogic.gdx.input.get-position :as get-position]))

(defn mouse-position [{:keys [ctx/input]}]
  (get-position/f input))
