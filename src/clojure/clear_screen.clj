(ns clojure.clear-screen
  (:require [clojure.graphics :as graphics]
            [clojure.gl20 :as gl20]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (graphics/get-gl20 graphics)]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit))
  ctx)
