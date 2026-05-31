(ns render.assoc-paused
  (:require [game.ctx :as ctx]
            [game.constants :refer [pausing?]]
            [game.state :as state]))

(defn step
  [{:keys [ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state/pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (ctx/key-just-pressed? ctx (:unpause-once controls))
                           (ctx/key-pressed? ctx (:unpause-continously controls))))))))
