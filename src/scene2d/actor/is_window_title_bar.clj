(ns scene2d.actor.is-window-title-bar
  (:require [scene2d.actor.get-parent :refer [get-parent]]
            [scene2d.ui.window.get-title-label :as get-title-label]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.window :as window]))

; FIXME does not work
(defn f [actor]
  (when (instance? label/java-class actor)
    (when-let [p (get-parent actor)]
      (when-let [p (get-parent p)]
        (and (instance? window/java-class actor)
             (= (get-title-label/f p) actor))))))
