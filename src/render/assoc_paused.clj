(ns render.assoc-paused
  (:require [ctx.key-pressed :refer [key-pressed?]]
            [ctx.key-just-pressed :refer [key-just-pressed?]]))

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
  [{:keys [ctx/pausing?
           ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (key-just-pressed? ctx (:unpause-once controls))
                           (key-pressed? ctx (:unpause-continously controls))))))))
