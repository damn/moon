(ns render.assoc-paused
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]
            [game.constants :refer [pausing?]]
            [game.state :as state]))

(defn step
  [{:keys [ctx/app
           ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state/pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (input/key-just-pressed? (app/input app) (:unpause-once controls))
                           (input/key-pressed? (app/input app) (:unpause-continously controls))))))))
