(ns render.window-camera-controls
  (:require [input.key-just-pressed :as key-just-pressed?]
            [input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

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
    (->> (Group/.findActor (:stage/root stage) "moon.ui.windows")
         Group/.getChildren
         (run! #(Actor/.setVisible % false))))

  (when (key-just-pressed?/f input (:toggle-inventory controls))
    (let [inventory (Group/.findActor (:stage/root stage) "moon.ui.windows.inventory")]
      (Actor/.setVisible inventory (not (Actor/.isVisible inventory)))))

  (when (key-just-pressed?/f input (:toggle-entity-info controls))
    (let [entity-info (Group/.findActor (:stage/root stage) "moon.ui.windows.entity-info")]
      (Actor/.setVisible entity-info (not (Actor/.isVisible entity-info)))))
  ctx)
