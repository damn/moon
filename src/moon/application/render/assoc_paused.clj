(ns moon.application.render.assoc-paused
  (:require [moon.input :as input]
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

(defmethod state/pause-game? :player-item-on-cursor
  [_]
  true)

(defmethod state/pause-game? :player-idle
  [_]
  true)

(defmethod state/pause-game? :player-dead
  [_]
  true)

(def pausing? true)

(defn step
  [{:keys [ctx/controls
           ctx/input
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state/pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (input/key-just-pressed? input (:unpause-once controls))
                           (input/key-pressed? input (:unpause-continously controls))))))))
