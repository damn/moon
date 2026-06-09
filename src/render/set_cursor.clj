(ns render.set-cursor
  (:require [com.badlogic.gdx.graphics.set-cursor :as set-cursor!]))

(defn step
  [{:keys [ctx/graphics
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
    (set-cursor!/f graphics (get cursors cursor-key)))
  ctx)
