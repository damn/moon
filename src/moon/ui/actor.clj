(ns moon.ui.actor)

(defprotocol Actor
  (set-opts! [_ opts]))

(defn toggle-visible! [^com.badlogic.gdx.scenes.scene2d.Actor actor]
  (.setVisible actor (not (.isVisible actor))))

(defn find-ancestor
  [^com.badlogic.gdx.scenes.scene2d.Actor actor clazz]
  (if-let [parent (.getParent actor)]
    (if (instance? clazz parent)
      parent
      (find-ancestor parent clazz))
    (throw (Error. (str "Actor has no parent window " actor)))))
