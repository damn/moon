(ns levelgen-test.app
  (:require [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.input.keys :as input.keys]
            [com.badlogic.gdx.utils.screen-utils :as screen-utils]
            [gdx.backends.lwjgl :as lwjgl]
            [gdx.graphics.orthographic-camera :as camera]
            [gdx.stage :as stage]
            [gdx.tiled-map-renderer :as tiled-map-renderer]
            [gdx.utils.disposable :as disposable]
            [gdx.viewport :as viewport]
            [levelgen-test.create :as create]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  ; TODO TEXTURES NOT DISPOSED
  (disposable/dispose! skin)
  (disposable/dispose! sprite-batch)
  (disposable/dispose! tiled-map))

(defn- draw-tiled-map! [{:keys [ctx/sprite-batch
                                ctx/color-setter
                                ctx/tiled-map
                                ctx/world-unit-scale
                                ctx/world-viewport]}]
  (tiled-map-renderer/draw! sprite-batch
                            world-unit-scale
                            (:viewport/camera world-viewport)
                            tiled-map
                            color-setter))

(defn- camera-movement-controls! [{:keys [ctx/input
                                          ctx/camera
                                          ctx/camera-movement-speed]}]
  (let [apply-position (fn [idx f]
                         (camera/set-position! camera
                                               (update (camera/position camera)
                                                       idx
                                                       #(f % camera-movement-speed))))]
    (if (input/key-pressed? input input.keys/left)  (apply-position 0 -))
    (if (input/key-pressed? input input.keys/right) (apply-position 0 +))
    (if (input/key-pressed? input input.keys/up)    (apply-position 1 +))
    (if (input/key-pressed? input input.keys/down)  (apply-position 1 -))))

(defn- camera-zoom-controls! [{:keys [ctx/input
                                      ctx/camera
                                      ctx/zoom-speed]}]
  (when (input/key-pressed? input input.keys/minus)  (camera/inc-zoom! camera zoom-speed))
  (when (input/key-pressed? input input.keys/equals) (camera/inc-zoom! camera (- zoom-speed))))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}
   _params]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (screen-utils/clear! 0 0 0 0)
    (draw-tiled-map! ctx)
    (camera-zoom-controls! ctx)
    (camera-movement-controls! ctx)
    (stage/set-ctx! stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))

(defn resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport    width height true)
  (viewport/update! world-viewport width height false))

(def state (atom nil))

(defn -main []
  (lwjgl/use-glfw-async!)
  (lwjgl/application!
   {:state-var #'state
    :create! create/f!
    :create-params nil
    :dispose! dispose!
    :render! render!
    :render-params nil
    :resize! resize!
    :title "Levelgen Test"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}))
