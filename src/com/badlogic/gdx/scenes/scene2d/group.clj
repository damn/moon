(ns com.badlogic.gdx.scenes.scene2d.group
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn create []
  (Group.))

(defn find-actor [^Group group name]
  (.findActor group name))

(defn clear-children! [^Group group]
  (.clearChildren group))

(defn get-children [^Group group]
  (.getChildren group))

(defn add-actor! [^Group group actor]
  (.addActor group actor))
