(ns clojure.moon.k-handle-input.player-idle
  (:require [com.badlogic.gdx.input :as input]
            [gdl.input.buttons :as input-buttons]
            [clojure.player-movement-vector :refer [player-movement-vector]]
            [clojure.interaction-state-txs :refer [interaction-state->txs]]))

(defn f
  [player-eid {:keys [ctx/input
                      ctx/interaction-state
                      ctx/stage]
               :as ctx}]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (input/isButtonJustPressed input (input-buttons/key-to-value :input.buttons/left))
      (interaction-state->txs interaction-state
                              stage
                              player-eid))))
