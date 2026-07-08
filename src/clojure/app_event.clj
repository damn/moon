(ns clojure.app-event
  (:require
            [clojure.scene2d.actor.get-stage]
            [clojure.apply-ctx :as apply-ctx]))

(defn f [f]
  (fn [_event actor]
    (apply-ctx/f (clojure.scene2d.actor.get-stage/f actor)
                 f)))
