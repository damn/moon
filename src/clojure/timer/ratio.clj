(ns clojure.timer.ratio
  (:require [clojure.timer.stopped :refer [stopped?]]))

(defn f [elapsed-time {:keys [duration stop-time] :as timer}]
  {:post [(<= 0 % 1)]}
  (if (stopped? elapsed-time timer)
    0
    ; min 1 because floating point math inaccuracies
    (min 1 (/ (- stop-time elapsed-time) duration))))
