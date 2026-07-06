(ns ctx.render.clear-screen
  (:require [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (graphics/get-gl20 graphics)]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit))
  ctx)
