(ns moon.ui.group
  (:require [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn find-actor [^Group group actor-name] ; TODO delete
  (.findActor group actor-name))

(defn children [^Group group] ; TODO delete
  (.getChildren group))

(defn set-opts! [^Group group opts]
  (run! (fn [actor]
          (.addActor group actor))
        (:group/actors opts)) ; TODO this is a function add-actors! seq hidden here
  (actor/set-opts! group opts))
