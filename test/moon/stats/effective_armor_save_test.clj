(ns moon.stats.effective-armor-save-test
  (:require [moon.stats :as stats]))

(comment

 (stats/effective-armor-save {} {:stats/modifiers {:modifiers/armor-save {:op/inc 10}}
                             :stats/armor-save 0})
 ; broken
 (let [source* {:stats/armor-pierce 0.4}
       target* {:stats/armor-save   0.5}]
   (stats/effective-armor-save source* target*))
 )
