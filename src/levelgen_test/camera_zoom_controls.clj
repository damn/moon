(ns levelgen-test.camera-zoom-controls
  (:require [input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]]))

(defn f
  [{:keys [ctx/input
           ctx/camera
           ctx/zoom-speed]}]
  (when (key-pressed?/f input :input.keys/minus)  (inc-zoom! camera zoom-speed))
  (when (key-pressed?/f input :input.keys/equals) (inc-zoom! camera (- zoom-speed))))
