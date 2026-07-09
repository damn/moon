(ns clojure.moon.k-handle-input.player-idle
  (:require [clojure.input-button-just-pressed :as button-just-pressed?]
            [clojure.player-movement-vector :refer [player-movement-vector]]
            [clojure.interaction-state-txs :refer [interaction-state->txs]]))

(defn f
  [player-eid {:keys [ctx/input
                      ctx/interaction-state
                      ctx/stage]
               :as ctx}]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (button-just-pressed?/f input :input.buttons/left)
      (interaction-state->txs interaction-state
                              stage
                              player-eid))))
