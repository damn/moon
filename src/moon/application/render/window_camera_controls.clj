(ns moon.application.render.window-camera-controls
  (:require [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]
            [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.group :as group]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.input :as input]))

(def zoom-speed 0.025)

(defn step
  [{:keys [ctx/controls
           ctx/input
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in controls))
    (camera/inc-zoom! (viewport/camera world-viewport) zoom-speed))

  (when (input/key-pressed? input (:zoom-out controls))
    (camera/inc-zoom! (viewport/camera world-viewport) (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key controls))
    (->> (stage/find-actor stage "moon.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (when (input/key-just-pressed? input (:toggle-inventory controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (input/key-just-pressed? input (:toggle-entity-info controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)
