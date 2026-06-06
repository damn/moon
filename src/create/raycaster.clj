(ns create.raycaster
  (:require [moon.raycaster :as raycaster]))

(defn step
  [{:keys [ctx/grid]}]
  (raycaster/create grid))
