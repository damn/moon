(ns clojure.max-speed)

(defn step
  [{:keys [ctx/minimum-size
           ctx/max-delta]}]
  (/ minimum-size max-delta))
