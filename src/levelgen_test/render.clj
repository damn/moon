(ns levelgen-test.render
  (:require [com.badlogic.gdx.scenes.scene2d.stage :as stage]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.color :as color]
            [gdx.draw-tiled-map :as draw-tiled-map]
            [com.badlogic.gdx.graphics.gl20 :as gl20]
            [input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]]
            [orthographic-camera.position :as get-position]
            [orthographic-camera.set-position :refer [set-position!]]
            [scene2d.stage :refer [set-ctx!]]))

(defn f
  [{:keys [ctx/input
           ctx/camera
           ctx/zoom-speed
           ctx/camera-movement-speed
           ctx/sprite-batch
           ctx/tiled-map
           ctx/world-viewport
           ctx/world-unit-scale
           ctx/graphics
           ctx/stage] :as ctx}]
  (let [ctx (or (:stage/ctx stage)
                ctx)] ; first render stage does not have ctx set. ( TODO: just set it ?  )
    (set-ctx! stage ctx))
  (let [gl (graphics/get-gl20 graphics)]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit))
  (draw-tiled-map/f! sprite-batch
                     world-unit-scale
                     (:viewport/camera world-viewport)
                     tiled-map
                     (constantly (color/float-bits [1 1 1 1])))
  (when (key-pressed?/f input :input.keys/minus)  (inc-zoom! camera zoom-speed))
  (when (key-pressed?/f input :input.keys/equals) (inc-zoom! camera (- zoom-speed)))
  (let [apply-position (fn [idx f]
                         (set-position! camera
                                        (update (get-position/f camera)
                                                idx
                                                #(f % camera-movement-speed))))]
    (if (key-pressed?/f input :input.keys/left)  (apply-position 0 -))
    (if (key-pressed?/f input :input.keys/right) (apply-position 0 +))
    (if (key-pressed?/f input :input.keys/up)    (apply-position 1 +))
    (if (key-pressed?/f input :input.keys/down)  (apply-position 1 -)))
  (stage/act! stage)
  (stage/draw! stage)
  (:stage/ctx stage))
