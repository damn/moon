(ns clojure.increment)

(defn f [timer duration]
  (update timer :stop-time + duration))
