(ns info.entity.temp-modifier
  (:require [moon.number :as number]
            [moon.timer :as timer]))

(defn f [{:keys [counter]} {:keys [ctx/elapsed-time]}]
  (str "Spiderweb - remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
