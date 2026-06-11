(ns render.clear-screen
  (:require [com.badlogic.gdx.graphics.clear :as clear]))

(defn step
  [{:keys [ctx/graphics]}]
  (clear/f! graphics 0 0 0 0)
  ctx)
