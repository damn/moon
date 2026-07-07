(ns clojure.enter-active-skill
  (:require [clojure.pay-mana-cost :as pay-mana-cost]))

(defn f
  [{:keys [skill]} eid]
  [[:tx/sound (:skill/start-action-sound skill)]
   [:tx/set-cooldown eid skill]
   [:tx/update eid :entity/stats pay-mana-cost/f (:skill/cost skill)]])
