(ns moon.create.ui-viewport
  (:require [clojure.gdx.ui-viewport :as ui-viewport]))

(defn step [ctx {:keys [width height]}]
  (assoc ctx :ctx/ui-viewport (ui-viewport/create width height)))
