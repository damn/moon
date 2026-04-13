(ns moon.ui.group
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as gdx-group]
            [moon.ui.actor :as actor]
            [moon.group :as group]))

(defn set-opts! [group opts]
  (when-let [actors (:group/actors opts)]
    (group/add-actors! group actors))
  (actor/set-opts! group opts))

(defn create [opts]
  (doto (gdx-group/create)
    (set-opts! opts)))
