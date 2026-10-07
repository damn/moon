(ns game.entity
  (:require [moon.timer :as timer]))

(defn add-text-effect [entity elapsed-time text duration]
  (assoc entity :entity/string-effect
         (if-let [existing (:entity/string-effect entity)]
           (-> existing
               (update :text str "\n" text)
               (update :counter timer/increment duration))
           {:text text
            :counter (timer/create elapsed-time duration)})))
