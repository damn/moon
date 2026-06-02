(ns moon.timer
  (:require [clojure.timer.stopped :refer [stopped?]]))

(defn reset [elapsed-time {:keys [duration] :as timer}]
  (assoc timer :stop-time (+ elapsed-time duration)))

(defn ratio [elapsed-time {:keys [duration stop-time] :as timer}]
  {:post [(<= 0 % 1)]}
  (if (stopped? elapsed-time timer)
    0
    ; min 1 because floating point math inaccuracies
    (min 1 (/ (- stop-time elapsed-time) duration))))

(defn increment [timer duration]
  (update timer :stop-time + duration))
