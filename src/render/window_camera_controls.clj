(ns render.window-camera-controls
  (:require [clojure.gdx.actor.set-visible :as set-visible]
            [clojure.gdx.actor.visible? :as visible?]
            [clojure.gdx.group.find-actor :as find-actor]
            [clojure.gdx.group.get-children :as get-children]
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
    (->> (find-actor/f (:stage/root stage) "moon.ui.windows")
         get-children/f
         (run! #(set-visible/f % false))))

  (when (key-just-pressed?/f input (:toggle-inventory controls))
    (let [inventory (find-actor/f (:stage/root stage) "moon.ui.windows.inventory")]
      (set-visible/f inventory (not (visible?/f inventory)))))

  (when (key-just-pressed?/f input (:toggle-entity-info controls))
    (let [entity-info (find-actor/f (:stage/root stage) "moon.ui.windows.entity-info")]
      (set-visible/f entity-info (not (visible?/f entity-info)))))
  ctx)
