(ns moon.create.ui-viewport
  (:require [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]))

(defn step [ctx]
  (assoc ctx :ctx/ui-viewport (viewport/create 1440 900 (camera/create))))
