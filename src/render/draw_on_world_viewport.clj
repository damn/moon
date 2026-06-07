(ns render.draw-on-world-viewport
  (:require [game.ctx.draw :refer [draw!]]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [com.badlogic.gdx.graphics.orthographic-camera.get-combined :refer [get-combined]]
            [space.earlygrey.shape-drawer :as shape-drawer]))

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
                          (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
                            (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
                            (reset! unit-scale world-unit-scale)
                            (doseq [f draw-fns]
                              (draw! ctx (f ctx)))
                            (reset! unit-scale 1)
                            (shape-drawer/set-default-line-width! shape-drawer old-line-width))                        ))

  ctx)
