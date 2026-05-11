(ns moon.application.render.clear-screen
  (:require [moon.graphics :as graphics]))

(defn step [{:keys [ctx/app] :as ctx}]
  (graphics/clear! app 0 0 0 0)
  ctx)
