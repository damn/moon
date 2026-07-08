(ns clojure.effective-armor-save
  (:require [clojure.stats.get-stat-value :refer [get-stat-value]]))

; not in stats because projectile as source doesnt have stats
; FIXME I don't see it triggering with 10 armor save ... !
(defn f [source-stats target-stats]
  (max (- (or (get-stat-value source-stats :stats/armor-save)   0)
          (or (get-stat-value target-stats :stats/armor-pierce) 0))
       0))
