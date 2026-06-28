(ns render.set-cursor
  (:import (com.badlogic.gdx Graphics)))

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
    (Graphics/.setCursor graphics (get cursors cursor-key)))
  ctx)
