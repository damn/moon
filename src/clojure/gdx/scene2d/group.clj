(ns clojure.gdx.scene2d.group
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.group :as group])
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

(defn create [opts]
  (doto (Group.)
    (group/set-opts! opts)))
