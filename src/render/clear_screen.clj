(ns render.clear-screen
  (:require [graphics.clear :as clear]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (clear/f! graphics 0 0 0 0)
  ctx)
