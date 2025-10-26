(ns moon.timer)

(defn create [elapsed-time duration]
  {:pre [(>= duration 0)]}
  {:duration duration
   :stop-time (+ elapsed-time duration)})

(defn stopped? [elapsed-time {:keys [stop-time]}]
  (>= elapsed-time stop-time))

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
