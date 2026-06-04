(ns game.ctx.graphics-delta-time
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.graphics :as graphics]))

(defn graphics-delta-time
  [{:keys [ctx/app]}]
  (graphics/delta-time (app/graphics app)))
