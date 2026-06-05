(ns game.ctx.graphics-delta-time
  (:require [clojure.application :as app]
            [clojure.graphics :as graphics]))

(defn graphics-delta-time
  [{:keys [ctx/app]}]
  (graphics/delta-time (app/graphics app)))
