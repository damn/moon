(ns gdx.actor.group
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn create []
  (Group.))

(defn find-actor [group actor-name]
  (.findActor ^Group group actor-name))

(defn get-children [group]
  (.getChildren ^Group group))

(defn add-actor! [group actor]
  (.addActor ^Group group ^Actor actor))

(defn clear-children! [group]
  (.clearChildren ^Group group))
