(ns render.clear-screen
  (:require [clojure.gdx :refer [get-gl20
                                 clear-color!
                                 clear!
                                 color-buffer-bit]]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (get-gl20 graphics)]
    (clear-color! gl 0 0 0 0)
    (clear! gl color-buffer-bit))
  ctx)
