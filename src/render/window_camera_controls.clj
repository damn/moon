(ns render.window-camera-controls
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [gdx.input.key-just-pressed :as key-just-pressed?]
            [gdx.input.key-pressed :as key-pressed?]
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
    (->> (group/find-actor (:stage/root stage) "moon.ui.windows")
         group/get-children
         (run! #(actor/set-visible! % false))))

  (when (key-just-pressed?/f input (:toggle-inventory controls))
    (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
      (actor/set-visible! inventory (not (actor/visible? inventory)))))

  (when (key-just-pressed?/f input (:toggle-entity-info controls))
    (let [entity-info (group/find-actor (:stage/root stage) "moon.ui.windows.entity-info")]
      (actor/set-visible! entity-info (not (actor/visible? entity-info)))))
  ctx)
