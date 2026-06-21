(ns info.entity.delete-after-duration
  (:require [clojure.readable :as readable]
            [moon.timer :as timer]))

(defn f [counter {:keys [ctx/elapsed-time]}]
  (str "Remaining: " (readable/f (timer/ratio elapsed-time counter)) "/1"))
