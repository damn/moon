(ns entity.state.enter.active-skill
  (:require [moon.stats :as stats]))

(defn f
  [{:keys [skill]} eid]
  [[:tx/sound (:skill/start-action-sound skill)]
   [:tx/set-cooldown eid skill]
   [:tx/update eid :entity/stats stats/pay-mana-cost (:skill/cost skill)]])
