(ns render.clear-screen
  (:require [gdx.graphics.clear-screen! :as clear-screen!]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (clear-screen!/f graphics)
  ctx)
