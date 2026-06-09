(ns game.ctx.clear-screen
  (:require [com.badlogic.gdx.graphics.get-gl20 :as get-gl20]
            [com.badlogic.gdx.graphics.gl20 :as gl20]))

(defn clear-screen!
  [{:keys [ctx/graphics]} r g b a]
  (let [gl (get-gl20/f graphics)]
    (gl20/clear-color! gl r g b a)
    (gl20/clear! gl gl20/color-buffer-bit)))
