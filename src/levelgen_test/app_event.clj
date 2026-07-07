(ns levelgen-test.app-event
  (:require [clojure.actor :as actor]
            [levelgen-test.apply-ctx :as apply-ctx]))

(defn f [f]
  (fn [_event actor]
    (apply-ctx/f (actor/get-stage actor)
                 f)))
