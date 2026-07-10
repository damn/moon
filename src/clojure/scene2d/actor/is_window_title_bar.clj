(ns clojure.scene2d.actor.is-window-title-bar
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor] [com.badlogic.gdx.scenes.scene2d.ui.window :as window]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]))

(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (actor/getParent actor)]
      (when-let [p (actor/getParent p)]
        (and (instance? window/class p)
             (= (window/getTitleLabel p) actor))))))
