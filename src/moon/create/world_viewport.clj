(ns moon.create.world-viewport
  (:require [clojure.gdx.world-viewport :as world-viewport]))

(defn step
  [{:keys [ctx/world-unit-scale]
    :as ctx}
   {:keys [width height]}]
  (assoc ctx :ctx/world-viewport (world-viewport/create (* width world-unit-scale)
                                                        (* height world-unit-scale))))
