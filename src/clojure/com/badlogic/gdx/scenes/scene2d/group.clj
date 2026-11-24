(ns clojure.com.badlogic.gdx.scenes.scene2d.group
  "2D scene graph node that may contain other actors.
  
  Actors have a z-order equal to the order they were inserted into the group. Actors inserted later will be drawn on top of
  actors added earlier. Touch events that hit more than one actor are distributed to topmost actors first."
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn create []
  (Group.))

(defn add-actor!
  "Adds an actor as a child of this group, removing it from its previous parent. If the actor is already a child of this
  group, no changes are made."
  [^Group group ^Actor actor]
  (.addActor group actor))

(defn find-actor [^Group group actor-name]
  (.findActor group actor-name))

(defn children
  "Returns an ordered list of child actors in this group."
  [^Group group]
  (.getChildren group))

(defn clear-children!
  "Removes all actors from this group and unfocuses them. Calls [clear-children!] with true."
  [^Group group]
  (.clearChildren group))
