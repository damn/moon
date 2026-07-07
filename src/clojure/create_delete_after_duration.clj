(ns clojure.create-delete-after-duration
  (:require [clojure.timer-create :refer [create-timer]]))

(defn f
  [duration {:keys [ctx/elapsed-time]}]
  (create-timer elapsed-time duration))
