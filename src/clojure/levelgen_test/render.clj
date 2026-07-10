(ns clojure.levelgen-test.render
  (:require [clojure.rgba.float-bits]
            [clojure.batch.draw-tiled-map :as draw-tiled-map]
            [gdl.gl20 :as gl20]
            [gdl.graphics :as graphics]
            [clojure.inc-zoom :refer [inc-zoom!]]
            [clojure.input.key-pressed :as key-pressed?]
            [clojure.orthographic-camera-position :as get-position]
            [clojure.orthographic-camera-set-position :refer [set-position!]]
            [clojure.stage :as stage]
            [clojure.viewport :as viewport]))

(defn render
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
  (let [gl (graphics/get-gl20 graphics)]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit))
  (draw-tiled-map/f! sprite-batch
                     world-unit-scale
                     (viewport/get-camera world-viewport)
                     tiled-map
                     (constantly (clojure.rgba.float-bits/f [1 1 1 1])))
  (when (key-pressed?/f input :input.keys/minus) (inc-zoom! camera zoom-speed))
  (when (key-pressed?/f input :input.keys/equals) (inc-zoom! camera (- zoom-speed)))
  (let [apply-position (fn [idx f]
                         (set-position! camera
                                        (update (get-position/f camera)
                                                idx
                                                #(f % camera-movement-speed))))]
    (if (key-pressed?/f input :input.keys/left) (apply-position 0 -))
    (if (key-pressed?/f input :input.keys/right) (apply-position 0 +))
    (if (key-pressed?/f input :input.keys/up) (apply-position 1 +))
    (if (key-pressed?/f input :input.keys/down) (apply-position 1 -)))
  (stage/act! stage)
  (stage/draw! stage)
  ctx)
