(ns game.render.assoc-paused
  (:require [game.ctx :as ctx]
            [moon.state :as state]))

(defmethod state/pause-game? :active-skill
  [_]
  false)

(defmethod state/pause-game? :stunned
  [_]
  false)

(defmethod state/pause-game? :player-moving
  [_]
  false)

(defmethod state/pause-game? :player-idle
  [_]
  true)

(defmethod state/pause-game? :player-dead
  [_]
  true)

(def pausing? true)

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
