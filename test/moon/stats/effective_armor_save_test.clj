(ns moon.stats.effective-armor-save-test
  (:require [moon.stats.effective-armor-save :as effective-armor-save]))

(comment

 (effective-armor-save/f {} {:stats/modifiers {:modifiers/armor-save {:op/inc 10}}
                             :stats/armor-save 0})
 ; broken
 (let [source* {:stats/armor-pierce 0.4}
       target* {:stats/armor-save   0.5}]
   (effective-armor-save/f source* target*))
 )
