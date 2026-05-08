(ns clojure.gdx.scene2d.group
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [moon.ui.actor :as actor]
            moon.ui.group))

(extend com.badlogic.gdx.scenes.scene2d.Group
  moon.ui.group/Group
  {:add-actor! group/add-actor!
   :children group/children
   :find-actor group/find-actor
   :clear-children! group/clear-children!
   :set-opts! (fn [group opts]
                (when-let [actors (:group/actors opts)]
                  (run! #(moon.ui.group/add-actor! group %) actors))
                (actor/set-opts! group opts))})

(defmethod actor/create :ui/group [opts]
  (doto (group/create)
    (moon.ui.group/set-opts! opts)))
