(ns ctx.mouse-position
  (:require [gdl.get-position :as get-position]))

(defn mouse-position [{:keys [ctx/input]}]
  (get-position/f input))
