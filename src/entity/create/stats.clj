(ns entity.create.stats
  (:require [game.entity :as entity]
            [moon.stats :as stats]))

(defmethod entity/create :entity/stats
  [[_ v] _ctx]
  (stats/create v))
