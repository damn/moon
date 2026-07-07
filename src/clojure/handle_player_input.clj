(ns clojure.handle-player-input
  (:require [clojure.ctx-do :refer [do!]]
            [clojure.k-handle-input :refer [k->handle-input]]))

(defn step
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (if-let [f (k->handle-input state-k)]
              (f eid ctx)
              nil)]
    (do! ctx txs))
  ctx)
