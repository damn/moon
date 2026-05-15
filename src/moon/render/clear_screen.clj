(ns moon.render.clear-screen
  (:require [clojure.gdx.app :as app]))

(defn step [{:keys [ctx/app] :as ctx}]
  (app/clear! app 0 0 0 0)
  ctx)
