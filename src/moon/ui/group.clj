(ns moon.ui.group
  (:require [moon.ui.actor :as actor]
            [moon.group :as group]))

(defn set-opts! [group opts]
  (when-let [actors (:group/actors opts)]
    (group/add-actors! group actors))
  (actor/set-opts! group opts))
