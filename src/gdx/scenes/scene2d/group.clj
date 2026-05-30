(ns gdx.scenes.scene2d.group
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.actor :as actor]))

(defn add-actor! [group actor]
  (group/add-actor! group actor))

(defn children [group]
  (group/children group))

(defn find-actor [group name]
  (group/find-actor group name))

(defn clear-children! [group]
  (group/clear-children! group))

(defn set-opts! [group opts]
  (when-let [actors (:group/actors opts)]
    (run! #(add-actor! group %) actors))
  (actor/set-opts! group opts))

 defn create
