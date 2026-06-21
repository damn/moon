(ns info.entity.temp-modifier
  (:require [clojure.readable :as readable]
            [moon.timer :as timer]))

(defn f [{:keys [counter]} {:keys [ctx/elapsed-time]}]
  (str "Spiderweb - remaining: " (readable/f (timer/ratio elapsed-time counter)) "/1"))
