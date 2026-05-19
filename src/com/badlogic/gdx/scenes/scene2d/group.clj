(ns com.badlogic.gdx.scenes.scene2d.group
  (:require [gdl.scene2d.actor :as actor]
            [gdl.scene2d.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn create [opts]
  (doto (Group.)
    (group/set-opts! opts)))

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
      (run! #(group/add-actor! group (actor/create %)) actors))
    (actor/set-opts! group opts)))
