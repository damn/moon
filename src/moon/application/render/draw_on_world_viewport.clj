(ns moon.application.render.draw-on-world-viewport
  (:require [moon.draws :as draws]
            [moon.graphics :as graphics]))

(defn step
  [ctx draw-fns]
  (graphics/draw-on-world-viewport! ctx
                                    (fn []
                                      (doseq [f draw-fns]
                                        (draws/handle ctx (f ctx)))))
  ctx)
