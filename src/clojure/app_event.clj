(ns clojure.app-event
  (:require
            [clojure.get-stage]
            [clojure.apply-ctx :as apply-ctx]))

(defn f [f]
  (fn [_event actor]
    (apply-ctx/f (clojure.get-stage/f actor)
                 f)))
