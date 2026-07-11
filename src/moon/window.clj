(ns moon.window
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

(defn title-bar? [actor]
  (when (instance? label/class actor)
    (when-let [p (actor/getParent actor)]
      (when-let [p (actor/getParent p)]
        (and (instance? window/class p)
             (= (window/getTitleLabel p) actor))))))
