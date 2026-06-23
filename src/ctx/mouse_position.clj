(ns ctx.mouse-position
  (:require [input.position :as get-position]))

(defn mouse-position [{:keys [ctx/input]}]
  (get-position/f input))
