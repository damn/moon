(ns game.ctx.graphics-delta-time
  (:require [gdx.application :as app]
            [gdx.graphics :as graphics]))

(defn graphics-delta-time
  [{:keys [ctx/app]}]
  (graphics/delta-time (app/graphics app)))
