(ns game.render.clear-screen
  (:require [gdl.app :as app]
            [gdl.graphics :as graphics]
            [gdl.graphics.gl20 :as gl20]))

(defn step [{:keys [ctx/app] :as ctx}]
  (let [gl (graphics/gl20 (app/graphics app))]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit))
  ctx)
