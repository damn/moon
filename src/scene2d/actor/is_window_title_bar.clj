(ns scene2d.actor.is-window-title-bar
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Button
                                               Label
                                               Window)))

(defn f [actor]
  (when (instance? Label actor)
    (when-let [p (Actor/.getParent actor)]
      (when-let [p (Actor/.getParent p)]
        (and (instance? Window actor)
             (= (Window/.getTitleLabel p) actor))))))
