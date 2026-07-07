(ns ctx.info.entity.delete-after-duration
  (:require [clojure.readable :as readable]
            [clojure.ratio :as ratio]))

(defn f [counter {:keys [ctx/elapsed-time]}]
  (str "Remaining: " (readable/f (ratio/f elapsed-time counter)) "/1"))
