(ns render.window-camera-controls
  (:require [com.badlogic.gdx.input.key-just-pressed :as key-just-pressed?]
            [com.badlogic.gdx.input.key-pressed :as key-pressed?]
            [gdx.orthographic-camera.inc-zoom :refer [inc-zoom!]]
            [com.badlogic.gdx.scenes.scene2d.group.find-actor :refer [find-actor]]
            [com.badlogic.gdx.scenes.scene2d.actor.set-visible :refer [set-visible!]]
            [com.badlogic.gdx.scenes.scene2d.actor.toggle-visible :refer [toggle-visible!]]
            [com.badlogic.gdx.scenes.scene2d.group.children :refer [children]]))

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
