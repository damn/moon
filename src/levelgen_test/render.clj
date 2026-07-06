(ns levelgen-test.render
  (:require [clojure.gdx.color.float-bits :as float-bits]
            [clojure.gdx.draw-tiled-map :as draw-tiled-map]
            [clojure.gdx.gl20.clear :as clear!]
            [clojure.gdx.gl20.clear-color :as clear-color!]
            [clojure.gdx.gl20.color-buffer-bit :as color-buffer-bit]
            [clojure.gdx.graphics.get-gl20 :as get-gl20]
            [clojure.gdx.stage.act :as act]
            [clojure.gdx.stage.draw :as draw]
            [clojure.gdx.stage.set-ctx :as set-ctx]
            [input.key-pressed :as key-pressed?]
            [orthographic-camera.inc-zoom :refer [inc-zoom!]]
            [orthographic-camera.position :as get-position]
            [orthographic-camera.set-position :refer [set-position!]]))

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
    (set-ctx/f stage ctx))
  (let [gl (get-gl20/f graphics)]
    (clear-color!/f gl 0 0 0 0)
    (clear!/f gl color-buffer-bit/v))
  (draw-tiled-map/f! sprite-batch
                     world-unit-scale
                     (:viewport/camera world-viewport)
                     tiled-map
                     (constantly (float-bits/f [1 1 1 1])))
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
  (act/f stage)
  (draw/f stage)
  (:stage/ctx stage))
