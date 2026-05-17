(ns game.render.window-camera-controls
  (:require [moon.camera :as camera]
            [moon.stage :as stage]
            [moon.ui.actor :as actor]
            [moon.ui.group :as group]
            [clojure.gdx.app :as app]
            [clojure.input :as input]))

(def zoom-speed 0.025)

(defn step
  [{:keys [ctx/controls
           ctx/app
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (input/key-pressed? (app/input app) (:zoom-in controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (input/key-pressed? (app/input app) (:zoom-out controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (input/key-just-pressed? (app/input app) (:close-windows-key controls))
    (->> (stage/find-actor stage "moon.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (when (input/key-just-pressed? (app/input app) (:toggle-inventory controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (input/key-just-pressed? (app/input app) (:toggle-entity-info controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)
