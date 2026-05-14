(ns moon.ui.impl.group
  (:require [moon.ui.actor]
            [moon.ui.impl.actor])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn add-actor! [^Group group actor]
  (.addActor group actor))

(defn children [^Group group]
  (.getChildren group))

(defn find-actor [^Group group name]
  (.findActor group name))

(defn clear-children! [^Group group]
  (.clearChildren group))

(defn set-opts! [group opts]
  (when-let [actors (:group/actors opts)]
    (run! #(add-actor! group (moon.ui.actor/create %)) actors))
  (moon.ui.impl.actor/set-opts! group opts))

(defn create [opts]
  (doto (Group.)
    (set-opts! opts)))
