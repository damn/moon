(ns moon.application.render.draw-on-world-viewport
  (:require [moon.graphics :as graphics]))

(defn step
  [{:keys [ctx/graphics]
    :as ctx}
   draw-fns]
  (graphics/draw-on-world-vp! graphics
                              (fn []
                                (doseq [f draw-fns]
                                  (graphics/draw! graphics (f ctx)))))
  ctx)
