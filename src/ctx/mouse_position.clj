(ns ctx.mouse-position
  (:require [gdx.input.position :as get-position]))

(defn mouse-position [{:keys [ctx/input]}]
  (get-position/f input))
