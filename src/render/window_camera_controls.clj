(ns render.window-camera-controls
  (:require [clojure.gdx.actor.set-visible :as set-visible]
            [clojure.gdx.actor.visible? :as visible?]
            [input.key-just-pressed :as key-just-pressed?]
            [input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

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
         (run! #(set-visible/f % false))))

  (when (key-just-pressed?/f input (:toggle-inventory controls))
    (let [inventory (Group/.findActor (:stage/root stage) "moon.ui.windows.inventory")]
      (set-visible/f inventory (not (visible?/f inventory)))))

  (when (key-just-pressed?/f input (:toggle-entity-info controls))
    (let [entity-info (Group/.findActor (:stage/root stage) "moon.ui.windows.entity-info")]
      (set-visible/f entity-info (not (visible?/f entity-info)))))
  ctx)
