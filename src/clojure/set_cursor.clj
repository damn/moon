(ns clojure.set-cursor
  (:require [clojure.graphics :as graphics]
            [clojure.k-cursor :refer [k->cursor]]))

(defn step
  [{:keys [ctx/graphics
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        f (k->cursor state-k)
        cursor-key (if (keyword? f)
                     f
                     (f eid ctx))]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! graphics (get cursors cursor-key)))
  ctx)
