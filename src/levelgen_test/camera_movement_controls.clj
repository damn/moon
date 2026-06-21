(ns levelgen-test.camera-movement-controls
  (:require [com.badlogic.gdx.input.key-pressed :as key-pressed?]
            [com.badlogic.gdx.graphics.orthographic-camera.get-position :refer [get-position]]
            [com.badlogic.gdx.graphics.orthographic-camera.set-position :refer [set-position!]]))

(defn f
  [{:keys [ctx/input
           ctx/camera
           ctx/camera-movement-speed]}]
  (let [apply-position (fn [idx f]
                         (set-position! camera
                                        (update (get-position camera)
                                                idx
                                                #(f % camera-movement-speed))))]
    (if (key-pressed?/f input :input.keys/left)  (apply-position 0 -))
    (if (key-pressed?/f input :input.keys/right) (apply-position 0 +))
    (if (key-pressed?/f input :input.keys/up)    (apply-position 1 +))
    (if (key-pressed?/f input :input.keys/down)  (apply-position 1 -))))
