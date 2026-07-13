(ns gdx.scenes.scene2d.group
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]))

(defn create []
  (group/new))

(defn find-actor [group actor-name]
  (group/findActor group actor-name))

(defn get-children [group]
  (group/getChildren group))

(defn add-actor! [group actor]
  (group/addActor group actor))

(defn clear-children! [group]
  (group/clearChildren group))
