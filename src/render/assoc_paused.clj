(ns render.assoc-paused
  (:require [ctx.key-pressed :refer [key-pressed?]]
            [ctx.key-just-pressed :refer [key-just-pressed?]]))

(defn step
  [{:keys [ctx/pausing?
           ctx/controls
           ctx/player-eid
           ctx/state->pause-game?]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (key-just-pressed? ctx (:unpause-once controls))
                           (key-pressed? ctx (:unpause-continously controls))))))))
