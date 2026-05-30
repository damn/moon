(ns render.set-cursor
  (:require [gdx.app :as app]
            [game.state :as state]))

(defn step
  [{:keys [ctx/app
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state/cursor [state-k (state-k entity)] eid ctx)]
    (assert (contains? cursors cursor-key))
    (app/set-cursor! app (get cursors cursor-key)))
  ctx)
