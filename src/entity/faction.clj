(ns entity.faction
  (:require [core.component :refer [defcomponent]]
            [api.entity :as entity]
            [core.data :as data]))

(defcomponent :entity/faction (data/enum :good :evil))

(extend-type api.entity.Entity
  entity/Faction
  (enemy-faction [{:keys [entity/faction]}]
    (case faction
      :evil :good
      :good :evil))

  (friendly-faction [{:keys [entity/faction]}]
    faction))
