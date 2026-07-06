(ns render.clear-screen
  (:require [clojure.gdx.gl20.clear :as clear!]
            [clojure.gdx.gl20.clear-color :as clear-color!]
            [clojure.gdx.gl20.color-buffer-bit :as color-buffer-bit]
            [clojure.gdx.graphics.get-gl20 :as get-gl20]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (get-gl20/f graphics)]
    (clear-color!/f gl 0 0 0 0)
    (clear!/f gl color-buffer-bit/v))
  ctx)
