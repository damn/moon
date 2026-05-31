(ns entity.state.enter.active-skill
  (:require [game.state :as state]
            [moon.stats :as stats]))

(defmethod state/enter :active-skill
  [[_k {:keys [skill]}] eid]
  [[:tx/sound (:skill/start-action-sound skill)]
   [:tx/set-cooldown eid skill]
   [:tx/update eid :entity/stats stats/pay-mana-cost (:skill/cost skill)]])
