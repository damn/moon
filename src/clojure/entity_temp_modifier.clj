(ns clojure.entity-temp-modifier
  (:require [clojure.readable :as readable]
            [clojure.ratio :as ratio]))

(defn f [{:keys [counter]} {:keys [ctx/elapsed-time]}]
  (str "Spiderweb - remaining: " (readable/f (ratio/f elapsed-time counter)) "/1"))
