(ns render.clear-screen
  (:require [game.app :as app]))

(defn step
  [{:keys [ctx/app] :as ctx}]
  (app/clear-screen! app 0 0 0 0)
  ctx)
