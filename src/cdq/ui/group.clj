(ns cdq.ui.group
  (:require [cdq.ui.actor :as actor]
            [cdq.ui.stage :as stage]
            [moon.scene2d.group :as group]))

(defn set-opts! [group opts]
  (run! (fn [actor]
          (group/add-actor! group actor))
        (:group/actors opts))
  (actor/set-opts! group opts))

(defmethod stage/build :actor/group [opts]
  (doto (group/create)
    (set-opts! opts)))
