(ns game.render.window-camera-controls
  (:require [clojure.graphics.orthographic-camera :as camera]
            [clojure.scene2d.stage :as stage]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.group :as group]
            [game.ctx :as ctx]))

(def zoom-speed 0.025)

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
