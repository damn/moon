(ns render.clear-screen
  (:require [gdx.application :as app]
            [gdx.graphics :as graphics]))

(defn step
  [{:keys [ctx/app] :as ctx}]
  (graphics/clear! (app/graphics app) 0 0 0 0)
  ctx)
