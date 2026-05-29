(ns create.raycaster
  (:require [moon.raycaster :as raycaster]))

(defn step
  [{:keys [ctx/grid] :as ctx}]
  (assoc ctx :ctx/raycaster (raycaster/create grid)))
