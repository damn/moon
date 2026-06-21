(ns render.window-camera-controls
  (:require [clojure.input.key-just-pressed :as key-just-pressed?]
            [clojure.input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]]
            [clojure.group.find-actor :refer [find-actor]]
            [clojure.actor.set-visible :refer [set-visible!]]
            [clojure.actor.toggle-visible :refer [toggle-visible!]]
            [clojure.group.children :refer [children]]))

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
