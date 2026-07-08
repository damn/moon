(ns clojure.moon.handle-player-input
  (:require [clojure.moon.ctx-do :refer [do!]]
            [clojure.moon.k-handle-input :refer [k->handle-input]]))

(defn f
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (if-let [input-fn (k->handle-input state-k)]
              (input-fn eid ctx)
              nil)]
    (do! ctx txs))
  ctx)
