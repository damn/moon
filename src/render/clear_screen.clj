(ns render.clear-screen
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]))

(defn step
  [{:keys [ctx/app] :as ctx}]
  (let [gl (graphics/gl20 (app/graphics app))]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit))
  ctx)
