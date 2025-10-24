(ns clojure.gdx.scene2d.group
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn create []
  (Group.))

(defn find-actor [group actor-name]
  (Group/.findActor group actor-name))

(defn add-actor! [group actor]
  (Group/.addActor group actor))

(defn children [group]
  (Group/.getChildren group))

(defn clear-children! [group]
  (Group/.clearChildren group))
