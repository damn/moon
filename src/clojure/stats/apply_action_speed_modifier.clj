(ns clojure.stats.apply-action-speed-modifier
  (:require [clojure.stats.get-stat-value :refer [get-stat-value]]))

(defn f [stats skill action-time]
  (/ action-time
     (or (get-stat-value stats (:skill/action-time-modifier-key skill))
         1)))
