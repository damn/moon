(ns info.skill.cooling-down
  (:require [moon.number :as number]
            [moon.timer :as timer]))

(defn f [counter {:keys [ctx/elapsed-time]}]
  (str "Cooldown: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
