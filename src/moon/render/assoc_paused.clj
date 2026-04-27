(ns moon.render.assoc-paused
  (:require [clojure.input :as input]
            [moon.state :as state]))

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
