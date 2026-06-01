(ns render.window-camera-controls
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.input :as input]
            [gdx.graphics.orthographic-camera :as camera]
            [game.constants :refer [zoom-speed]]
            [gdx.stage :as stage]
            [gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scene2d.actor.set-visible :refer [set-visible!]]
            [gdx.scenes.scene2d.group :as group]))

(defn step
  [{:keys [ctx/app
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [input (app/input app)]
    (when (input/key-pressed? input (:zoom-in controls))
      (camera/inc-zoom! (:viewport/camera world-viewport) zoom-speed))

    (when (input/key-pressed? input (:zoom-out controls))
      (camera/inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

    (when (input/key-just-pressed? input (:close-windows-key controls))
      (->> (stage/find-actor stage "moon.ui.windows")
           group/children
           (run! #(set-visible! % false))))

    (when (input/key-just-pressed? input (:toggle-inventory controls))
      (-> stage
          (stage/find-actor "moon.ui.windows.inventory")
          actor/toggle-visible!))

    (when (input/key-just-pressed? input (:toggle-entity-info controls))
      (-> stage
          (stage/find-actor "moon.ui.windows.entity-info")
          actor/toggle-visible!)))
  ctx)
