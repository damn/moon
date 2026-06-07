(ns game.ctx.clear-screen
  (:require [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]))

(defn clear-screen!
  [{:keys [ctx/graphics]} r g b a]
  (let [gl (graphics/gl20 graphics)]
    (gl20/clear-color! gl r g b a)
    (gl20/clear! gl gl20/color-buffer-bit)))
