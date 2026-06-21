(ns game.ctx.mouse-position
  (:require [clojure.input.get-position :as get-position]))

(defn mouse-position [{:keys [ctx/input]}]
  (get-position/f input))
