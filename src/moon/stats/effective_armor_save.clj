(ns moon.stats.effective-armor-save
  (:require [moon.stats.get-stat-value :refer [get-stat-value]]))

; not in stats because projectile as source doesnt have stats
; FIXME I don't see it triggering with 10 armor save ... !
(defn f [source-stats target-stats]
  (max (- (or (get-stat-value source-stats :stats/armor-save)   0)
          (or (get-stat-value target-stats :stats/armor-pierce) 0))
       0))

(comment

 (effective-armor-save {} {:stats/modifiers {:modifiers/armor-save {:op/inc 10}}
                           :stats/armor-save 0})
 ; broken
 (let [source* {:stats/armor-pierce 0.4}
       target* {:stats/armor-save   0.5}]
   (effective-armor-save source* target*))
 )
