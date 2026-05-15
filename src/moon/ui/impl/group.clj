(ns moon.ui.impl.group
  (:require [moon.ui.actor]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn add-actor! [^Group group actor]
  (.addActor group actor)); TODO can actror/create and return it ?

(defn children [^Group group]
  (.getChildren group))

(defn find-actor [^Group group name]
  (.findActor group name))

(defn clear-children! [^Group group]
  (.clearChildren group))

(defn set-opts! [group opts]
  (when-let [actors (:group/actors opts)]
    (run! #(add-actor! group (moon.ui.actor/create %)) actors)) ; TODO this part of 'clojure.gdx' -> yes seems so !
  (actor/set-opts! group opts))

(defn create [opts]
  (doto (Group.)
    (set-opts! opts)))
