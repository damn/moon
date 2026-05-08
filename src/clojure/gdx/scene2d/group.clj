(ns clojure.gdx.scene2d.group
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.ui.actor :as actor]))

(def add-actor! group/add-actor!)
(def children group/children)
(def find-actor group/find-actor)
(def clear-children! group/clear-children!)

(defn set-opts! [group opts]
  (when-let [actors (:group/actors opts)]
    (run! #(add-actor! group %) actors))
  (actor/set-opts! group opts))

(defmethod actor/create :ui/group [opts]
  (doto (group/create)
    (set-opts! opts)))
