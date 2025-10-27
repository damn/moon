(ns moon.ui.group
  (:require [moon.scene2d.group :as group]
            [moon.ui.actor :as actor]
            [moon.ui.stage :as stage]))

(defn set-opts! [group opts]
  (run! (fn [actor]
          (group/add-actor! group actor))
        (:group/actors opts))
  (actor/set-opts! group opts))

(defmethod stage/build :actor/group [opts]
  (doto (group/create)
    (set-opts! opts)))
