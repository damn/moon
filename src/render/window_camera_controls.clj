(ns render.window-camera-controls
  (:require [gdl.input.key-just-pressed :as key-just-pressed?]
            [gdl.input.key-pressed :as key-pressed?]
            [gdl.orthographic-camera.inc-zoom :refer [inc-zoom!]]
            [gdl.group.find-actor :refer [find-actor]]
            [gdl.actor.set-visible :refer [set-visible!]]
            [gdl.actor.toggle-visible :refer [toggle-visible!]]
            [gdl.group.children :refer [children]]))

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
    (->> (find-actor (:stage/root stage) "moon.ui.windows")
         children
         (run! #(set-visible! % false))))

  (when (key-just-pressed?/f input (:toggle-inventory controls))
    (-> stage
        :stage/root
        (find-actor "moon.ui.windows.inventory")
        toggle-visible!))

  (when (key-just-pressed?/f input (:toggle-entity-info controls))
    (-> stage
        :stage/root
        (find-actor "moon.ui.windows.entity-info")
        toggle-visible!))
  ctx)
