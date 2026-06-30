(ns render.window-camera-controls
  (:require [clojure.gdx :as gdx]
            [input.key-just-pressed :as key-just-pressed?]
            [input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

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
         (run! #(Actor/.setVisible % false))))

  (when (key-just-pressed?/f input (:toggle-inventory controls))
    (let [inventory (gdx/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
      (Actor/.setVisible inventory (not (Actor/.isVisible inventory)))))

  (when (key-just-pressed?/f input (:toggle-entity-info controls))
    (let [entity-info (gdx/find-actor (:stage/root stage) "moon.ui.windows.entity-info")]
      (Actor/.setVisible entity-info (not (Actor/.isVisible entity-info)))))
  ctx)
