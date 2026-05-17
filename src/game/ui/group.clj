(ns game.ui.group
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.ui.actor :as actor]))

(def add-actor! group/add-actor!)
(def children group/children)
(def find-actor group/find-actor)
(def clear-children! group/clear-children!)

(defn set-opts! [group opts]
  (when-let [actors (:group/actors opts)]
    (run! #(add-actor! group (actor/create %)) actors))
  (actor/set-opts! group opts))

(defn create [opts]
  (doto (group/create)
    (set-opts! opts)))
