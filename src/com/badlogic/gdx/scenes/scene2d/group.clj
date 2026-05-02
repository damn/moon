(ns com.badlogic.gdx.scenes.scene2d.group
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn add-actor! [^Group group actor]
  (.addActor group actor))

(defn children [^Group group]
  (.getChildren group))

(defn find-actor [^Group group name]
  (.findActor group name))

(defn clear-children! [^Group group]
  (.clearChildren group))

(defn create []
  (Group.))
