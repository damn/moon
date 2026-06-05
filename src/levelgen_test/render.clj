(ns levelgen-test.render
  (:require [clojure.input :as input]
            [clojure.input.keys :as input.keys]
            [clojure.gdx.utils.screen-utils :as screen-utils]
            [gdx.graphics.orthographic-camera :as camera]
            [clojure.scene2d.stage.draw :refer [draw!]]
            [clojure.scene2d.stage.act :refer [act!]]
            [clojure.scene2d.stage.set-ctx :refer [set-ctx!]]
            [clojure.maps.tiled.renderer :as tiled-map-renderer]))

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
