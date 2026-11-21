(ns clojure.com.badlogic.gdx.scenes.scene2d.group
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn create []
  (Group.))

(defn add-actor! [^Group group ^Actor actor]
  (.addActor group actor))

(defn find-actor [^Group group actor-name]
  (.findActor group actor-name))

(defn children [^Group group]
  (.getChildren group))

(defn clear-children! [^Group group]
  (.clearChildren group))
