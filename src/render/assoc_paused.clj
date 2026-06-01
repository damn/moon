(ns render.assoc-paused
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]
            [game.constants :refer [pausing?]]))

(def state->pause-game?
  {
   :active-skill false
   :stunned false
   :player-moving false
   :player-idle true
   :player-dead true
   :player-item-on-cursor true
   })

(defn step
  [{:keys [ctx/app
           ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (input/key-just-pressed? (app/input app) (:unpause-once controls))
                           (input/key-pressed? (app/input app) (:unpause-continously controls))))))))
