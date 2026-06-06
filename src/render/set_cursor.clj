(ns render.set-cursor
  (:require [gdx.application :as app]
            [com.badlogic.gdx.graphics :as graphics]))

(defn step
  [{:keys [ctx/app
           ctx/cursors
           ctx/player-eid
           ctx/k->cursor]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        f (k->cursor state-k)
        cursor-key (if (keyword? f)
                     f
                     (f eid ctx))]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! (app/graphics app) (get cursors cursor-key)))
  ctx)
