(ns render.window-camera-controls
  (:require [clojure.gdx :as gdx]
            [input.key-just-pressed :as key-just-pressed?]
            [input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]]))

(defn step
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport
           ctx/zoom-speed]
    :as ctx}]
  (when (key-pressed?/f input (:zoom-in controls))
    (inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (key-pressed?/f input (:zoom-out controls))
    (inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (key-just-pressed?/f input (:close-windows-key controls))
    (->> (gdx/find-actor (:stage/root stage) "moon.ui.windows")
         gdx/get-children
         (run! #(gdx/set-visible! % false))))

  (when (key-just-pressed?/f input (:toggle-inventory controls))
    (let [inventory (gdx/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
      (gdx/set-visible! inventory (not (gdx/visible? inventory)))))

  (when (key-just-pressed?/f input (:toggle-entity-info controls))
    (let [entity-info (gdx/find-actor (:stage/root stage) "moon.ui.windows.entity-info")]
      (gdx/set-visible! entity-info (not (gdx/visible? entity-info)))))
  ctx)
