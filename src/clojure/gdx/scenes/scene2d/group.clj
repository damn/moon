(ns clojure.gdx.scenes.scene2d.group
  (:require [clojure.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defprotocol PGroup
  (add-actor! [_ actor])
  (children [_])
  (find-actor [_ name])
  (clear-children! [_])
  (set-opts! [_ opts]))

(extend-type Group
  PGroup
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
      (run! #(add-actor! group %) actors))
    (actor/set-opts! group opts)))

(defmethod actor/create :ui/group
  [opts]
  (doto (Group.)
    (set-opts! opts)))
