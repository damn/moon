(ns game.ctx.clear-screen
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.graphics :as graphics]
            [clojure.gdx.graphics.gl20 :as gl20]))

(defn clear-screen!
  [{:keys [ctx/app]} r g b a]
  (let [gl (graphics/gl20 (app/graphics app))]
    (gl20/clear-color! gl r g b a)
    (gl20/clear! gl gl20/color-buffer-bit)))
