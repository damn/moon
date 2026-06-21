(ns render.draw-on-world-viewport
  (:require [game.ctx.draw :refer [draw!]]
            [clojure.batch :as batch]
            [clojure.orthographic-camera.get-combined :refer [get-combined]]
            [clojure.shape-drawer.default-line-width :refer [default-line-width]]
            [clojure.shape-drawer.set-default-line-width :refer [set-default-line-width!]]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  (batch/setup-drawing! batch
                        (get-combined (:viewport/camera world-viewport))
                        (fn []
                          (let [old-line-width (default-line-width shape-drawer)]
                            (set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
                            (reset! unit-scale world-unit-scale)
                            (doseq [[f & params] draw-fns]
                              (draw! ctx (apply f ctx params)))
                            (reset! unit-scale 1)
                            (set-default-line-width! shape-drawer old-line-width))))
  ctx)
