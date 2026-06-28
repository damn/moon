(ns render.clear-screen
  (:require [gdx.graphics.clear :refer [clear!]]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (clear! graphics 0 0 0 0)
  ctx)
