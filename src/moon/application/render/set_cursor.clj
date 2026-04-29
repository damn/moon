(ns moon.application.render.set-cursor
  (:require [clojure.graphics :as graphics]
            [moon.state :as state]))

(defn step
  [{:keys [ctx/cursors
           ctx/graphics
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state/cursor [state-k (state-k entity)] eid ctx)]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! graphics (get cursors cursor-key)))
  ctx)
