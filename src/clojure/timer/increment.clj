(ns clojure.timer.increment)

(defn f [timer duration]
  (update timer :stop-time + duration))
