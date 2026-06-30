(ns render.clear-screen
  (:require [clojure.gdx :as gdx]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (gdx/clear! graphics 0 0 0 0)
  ctx)
