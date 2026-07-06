(ns render.assoc-paused
  (:require [gdx.input.key-pressed :refer [f] :rename {f key-pressed?}]
            [gdx.input.key-just-pressed :refer [f] :rename {f key-just-pressed?}]))

(defn step
  [{:keys [ctx/input
           ctx/pausing?
           ctx/controls
           ctx/player-eid
           ctx/state->pause-game?]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (key-just-pressed? input (:unpause-once controls))
                           (key-pressed? input (:unpause-continously controls))))))))
