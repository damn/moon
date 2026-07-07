(ns ctx.info.skill.cooling-down
  (:require [clojure.readable :as readable]
            [clojure.ratio :as ratio]))

(defn f [counter {:keys [ctx/elapsed-time]}]
  (str "Cooldown: " (readable/f (ratio/f elapsed-time counter)) "/1"))
