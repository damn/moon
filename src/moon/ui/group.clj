(ns moon.ui.group
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn find-actor [group actor-name]
  (Group/.findActor group actor-name))

(defn add-actor! [group actor]
  (Group/.addActor group actor))

(defn children [group]
  (Group/.getChildren group))

(defn clear-children! [group]
  (Group/.clearChildren group))

(defn set-opts! [group opts]
  (run! (fn [actor]
          (add-actor! group actor))
        (:group/actors opts))
  (actor/set-opts! group opts))

(defn create [opts]
  (doto (Group.)
    (set-opts! opts)))
