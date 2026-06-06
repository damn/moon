(ns game.ctx.clear-screen
  (:require [gdx.application :as app]
            [clojure.graphics :as graphics]
            [clojure.gl20 :as gl20]))

(defn clear-screen!
  [{:keys [ctx/app]} r g b a]
  (let [gl (graphics/gl20 (app/graphics app))]
    (gl20/clear-color! gl r g b a)
    (gl20/clear! gl gl20/color-buffer-bit)))
