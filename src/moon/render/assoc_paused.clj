(ns moon.render.assoc-paused
  (:require [moon.entity.state :as state]
            [moon.input :as input]))

(def pausing? true)

(defn do!
  [{:keys [ctx/input
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state/pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (input/key-just-pressed? input (:unpause-once input/controls))
                           (input/key-pressed? input (:unpause-continously input/controls))))))))
