(ns moon.application.render.window-camera-controls
  (:require [moon.camera :as camera]
            [moon.stage :as stage]
            [moon.ui.actor :as actor]
            [moon.ui.group :as group]
            [moon.input :as input]))

(def zoom-speed 0.025)

(defn step
  [{:keys [ctx/controls
           ctx/app
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (input/key-pressed? app (:zoom-in controls))
    (camera/inc-zoom! world-viewport zoom-speed))

  (when (input/key-pressed? app (:zoom-out controls))
    (camera/inc-zoom! world-viewport (- zoom-speed)))

  (when (input/key-just-pressed? app (:close-windows-key controls))
    (->> (stage/find-actor stage "moon.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (when (input/key-just-pressed? app (:toggle-inventory controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (input/key-just-pressed? app (:toggle-entity-info controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)
