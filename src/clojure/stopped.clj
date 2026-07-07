(ns clojure.stopped)

(defn stopped? [elapsed-time {:keys [stop-time]}]
  (>= elapsed-time stop-time))
