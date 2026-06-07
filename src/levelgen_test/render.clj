(ns levelgen-test.render
  (:require [gdx.input :as input]
            [gdx.screen :as screen-utils]
            [com.badlogic.gdx.graphics.orthographic-camera.inc-zoom :refer [inc-zoom!]]
            [com.badlogic.gdx.graphics.orthographic-camera.get-position :refer [get-position]]
            [com.badlogic.gdx.graphics.orthographic-camera.set-position :refer [set-position!]]
            [com.badlogic.gdx.scenes.scene2d.stage.draw :refer [draw!]]
            [com.badlogic.gdx.scenes.scene2d.stage.act :refer [act!]]
            [com.badlogic.gdx.scenes.scene2d.stage.set-ctx :refer [set-ctx!]]
            [gdx.draw-tiled-map :as batch]))

(defn- draw-tiled-map! [{:keys [ctx/sprite-batch
                                ctx/color-setter
                                ctx/tiled-map
                                ctx/world-unit-scale
                                ctx/world-viewport]}]
  (batch/draw-tiled-map! sprite-batch
                         world-unit-scale
                         (:viewport/camera world-viewport)
                         tiled-map
                         color-setter))

(defn- camera-movement-controls! [{:keys [ctx/input
                                          ctx/camera
                                          ctx/camera-movement-speed]}]
  (let [apply-position (fn [idx f]
                         (set-position! camera
                                        (update (get-position camera)
                                                idx
                                                #(f % camera-movement-speed))))]
    (if (input/key-pressed? input :input.keys/left)  (apply-position 0 -))
    (if (input/key-pressed? input :input.keys/right) (apply-position 0 +))
    (if (input/key-pressed? input :input.keys/up)    (apply-position 1 +))
    (if (input/key-pressed? input :input.keys/down)  (apply-position 1 -))))

(defn- camera-zoom-controls! [{:keys [ctx/input
                                      ctx/camera
                                      ctx/zoom-speed]}]
  (when (input/key-pressed? input :input.keys/minus)  (inc-zoom! camera zoom-speed))
  (when (input/key-pressed? input :input.keys/equals) (inc-zoom! camera (- zoom-speed))))

(defn f!
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (screen-utils/clear! 0 0 0 0)
    (draw-tiled-map! ctx)
    (camera-zoom-controls! ctx)
    (camera-movement-controls! ctx)
    (set-ctx! stage ctx)
    (act! stage)
    (draw! stage)
    (:stage/ctx stage)))
