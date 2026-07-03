(ns levelgen-test.app-event
  (:require [clojure.gdx.actor.get-stage :as get-stage]
            [levelgen-test.apply-ctx :as apply-ctx]))

(defn f [f]
  (fn [_event actor]
    (apply-ctx/f (get-stage/f actor)
                 f)))
