(ns moon.create.ui-viewport
  (:require [gdl.context :as context]))

(defn step [ctx {:keys [width height]}]
  (assoc ctx :ctx/ui-viewport (context/ui-viewport width height)))
