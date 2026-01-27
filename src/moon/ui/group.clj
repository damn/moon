(ns moon.ui.group
  (:require [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn find-actor [^Group group actor-name]
  (.findActor group actor-name))

(defn children [^Group group]
  (.getChildren group))

(defn set-opts! [^Group group opts]
  (run! (fn [actor]
          (.addActor group actor))
        (:group/actors opts))
  (actor/set-opts! group opts))
