(ns clojure.create-active-skill
  (:require [clojure.apply-action-speed-modifier :as apply-action-speed-modifier]
            [clojure.timer-create :refer [create-timer]]))

(defn f
  [[_k [skill effect-ctx]] eid {:keys [ctx/elapsed-time]}]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (apply-action-speed-modifier/f @eid skill)
                 (create-timer elapsed-time))})
