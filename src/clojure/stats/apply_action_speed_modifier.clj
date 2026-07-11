(ns clojure.stats.apply-action-speed-modifier
  (:require [moon.stats :as stats]))

(defn f [stats skill action-time]
  (/ action-time
     (or (stats/get-value stats (:skill/action-time-modifier-key skill))
         1)))
