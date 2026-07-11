(ns moon.timer)

(defn increment [timer duration]
  (update timer :stop-time + duration))
