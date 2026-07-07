(ns clojure.assoc-paused
  (:require [clojure.key-just-pressed :refer [f] :rename {f key-just-pressed?}]
            [clojure.key-pressed :refer [f] :rename {f key-pressed?}]
            [clojure.pausing :as pausing]
            [clojure.state-pause-game :as state-pause-game]))

(defn step
  [{:keys [ctx/input
           ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing/pausing?
                  (state-pause-game/state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (key-just-pressed? input (:unpause-once controls))
                           (key-pressed? input (:unpause-continously controls))))))))
