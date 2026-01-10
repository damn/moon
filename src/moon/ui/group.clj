(ns moon.ui.group
  (:require [gdl.ui.group :as group]
            [gdl.ui.stage :as stage]
            [moon.ui.actor :as actor]))

(defn set-opts! [group opts]
  (run! (fn [actor]
          (group/add-actor! group actor))
        (:group/actors opts))
  (actor/set-opts! group opts))

(defmethod stage/build :actor/group [opts]
  (doto (group/create)
    (set-opts! opts)))
