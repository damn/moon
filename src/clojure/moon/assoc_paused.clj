(ns clojure.moon.assoc-paused
  (:require [clojure.key-just-pressed :refer [f] :rename {f key-just-pressed?}]
            [clojure.key-pressed :refer [f] :rename {f key-pressed?}]
            [clojure.pausing :refer [pausing?]]
            [clojure.state-pause-game :refer [state->pause-game?]]))

(defn f
  [{:keys [ctx/input
           ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (key-just-pressed? input (:unpause-once controls))
                           (key-pressed? input (:unpause-continously controls))))))))
