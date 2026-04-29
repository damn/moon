(ns moon.application.render.draw-on-world-viewport
  (:require moon.application.render.draw-on-world-viewport.draw-tile-grid
            moon.application.render.draw-on-world-viewport.draw-cell-debug
            moon.application.render.draw-on-world-viewport.draw-entities
            moon.application.render.draw-on-world-viewport.highlight-mouseover-tile
            [moon.draws :as draws]
            [moon.graphics :as graphics]))

(defn step
  [ctx]
  (graphics/draw-on-world-viewport! ctx
                                    (fn []
                                      (doseq [f [
                                                 #_moon.application.render.draw-on-world-viewport.draw-tile-grid/draws
                                                 moon.application.render.draw-on-world-viewport.draw-cell-debug/draws
                                                 moon.application.render.draw-on-world-viewport.draw-entities/do!
                                                 #_moon.geom-test
                                                 moon.application.render.draw-on-world-viewport.highlight-mouseover-tile/draws
                                                 ]]
                                        (draws/handle ctx (f ctx)))))
  ctx)
