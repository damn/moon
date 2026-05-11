(ns clojure.gdx.scene2d.group
  (:require [moon.ui.actor :as actor]
            [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(extend-type Group
  group/Group
  (add-actor! [group actor]
    (.addActor group actor))

  (children [group]
    (.getChildren group))

  (find-actor [group name]
    (.findActor group name))

  (clear-children! [group]
    (.clearChildren group))

  (set-opts! [group opts]
    (when-let [actors (:group/actors opts)]
      (run! #(group/add-actor! group %) actors))
    (actor/set-opts! group opts)))

(defmethod actor/create :ui/group [opts]
  (doto (Group.)
    (group/set-opts! opts)))
