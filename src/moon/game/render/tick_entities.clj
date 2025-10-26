(ns moon.game.render.tick-entities
  (:require [moon.ui :as ui]
            [moon.world :as world]
            [moon.throwable :as throwable]
            [moon.txs :as txs]))

(defn step
  [{:keys [ctx/stage
           ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do (try
         (txs/handle! ctx (world/tick-entities! world))
         (catch Throwable t
           (throwable/pretty-pst t)
           (ui/show-error-window! stage t)))
        ctx)))
