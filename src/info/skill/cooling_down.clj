(ns info.skill.cooling-down
  (:require [clojure.readable :as readable]
            [moon.timer :as timer]))

(defn f [counter {:keys [ctx/elapsed-time]}]
  (str "Cooldown: " (readable/f (timer/ratio elapsed-time counter)) "/1"))
