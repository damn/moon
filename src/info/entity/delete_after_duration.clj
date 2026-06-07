(ns info.entity.delete-after-duration
  (:require [moon.number :as number]
            [moon.timer :as timer]))

(defn f [counter {:keys [ctx/elapsed-time]}]
  (str "Remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
