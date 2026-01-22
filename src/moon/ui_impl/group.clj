(ns moon.ui-impl.group
  (:require [moon.ui.actor :as actor]
            [moon.ui.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(extend-type Group
  group/Group
  (find-actor [group actor-name]
    (.findActor group actor-name))

  (children [group]
    (.getChildren group))

  (set-opts! [group opts]
    (run! (fn [actor]
            (.addActor group actor))
          (:group/actors opts))
    (actor/set-opts! group opts)))

(defn create [opts]
  (doto (Group.)
    (group/set-opts! opts)))
