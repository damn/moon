(ns moon.create.world-viewport
  (:require [gdl.context :as context]))

(defn step
  [{:keys [ctx/world-unit-scale]
    :as ctx}
   {:keys [width height]}]
  (assoc ctx :ctx/world-viewport (context/world-viewport (* width world-unit-scale)
                                                         (* height world-unit-scale))))
