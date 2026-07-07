(ns clojure.timer-create)

(defn create-timer
  [elapsed-time duration]
  {:pre [(>= duration 0)]}
  {:duration duration
   :stop-time (+ elapsed-time duration)})
