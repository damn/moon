(ns clojure.stats.effective-armor-save
  (:require [moon.stats :as stats]))

; not in stats because projectile as source doesnt have stats
; FIXME I don't see it triggering with 10 armor save ... !
(defn f [source-stats target-stats]
  (max (- (or (stats/get-value source-stats :stats/armor-save)   0)
          (or (stats/get-value target-stats :stats/armor-pierce) 0))
       0))
