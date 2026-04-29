(ns moon.application.create.max-speed)

(defn step
  [{:keys [ctx/minimum-size
           ctx/max-delta]
    :as ctx}]
  (assoc ctx :ctx/max-speed (/ minimum-size max-delta)))
