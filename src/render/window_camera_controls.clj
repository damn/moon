(ns render.window-camera-controls
  (:require [game.ctx :as ctx]
            [gdx.graphics.orthographic-camera :as camera]
            [game.constants :refer [zoom-speed]]
            [gdx.stage :as stage]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.group :as group]))

(defn step
  [{:keys [ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (ctx/key-pressed? ctx (:zoom-in controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (ctx/key-pressed? ctx (:zoom-out controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (ctx/key-just-pressed? ctx (:close-windows-key controls))
    (->> (stage/find-actor stage "moon.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (when (ctx/key-just-pressed? ctx (:toggle-inventory controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (ctx/key-just-pressed? ctx (:toggle-entity-info controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)
