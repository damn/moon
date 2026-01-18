(ns moon.ui.actor)

(defprotocol Actor
  (set-opts! [_ opts]))

(defn toggle-visible! [^com.badlogic.gdx.scenes.scene2d.Actor actor]
  (.setVisible actor (not (.isVisible actor))))
