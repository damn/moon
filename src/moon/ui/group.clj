(ns moon.ui.group
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn set-opts! [^Group group opts]
  (run! (fn [actor]
          (.addActor group actor))
        (:group/actors opts)) ; TODO this is a function add-actors! seq hidden here
  group)
