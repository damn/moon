(ns clojure.moon.assoc-paused
  (:require [com.badlogic.gdx.input :as input]
            [gdl.input.keys :as input-keys]
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
                  (not (or (input/isKeyJustPressed input (input-keys/key-to-value (:unpause-once controls)))
                           (input/isKeyPressed input (input-keys/key-to-value (:unpause-continously controls)))))))))
