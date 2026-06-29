(ns render.clear-screen
  (:require [clojure.gdx.clear-color-buffer :as clear-color-buffer]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (clear-color-buffer/f! graphics 0 0 0 0)
  ctx)
