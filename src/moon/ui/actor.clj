(ns moon.ui.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-position! [^Actor actor [x y]]
  (.setPosition actor x y))

(defn toggle-visible! [^Actor actor]
  (.setVisible actor (not (.isVisible actor))))

(defn find-ancestor
  [^Actor actor clazz]
  (if-let [parent (.getParent actor)]
    (if (instance? clazz parent)
      parent
      (find-ancestor parent clazz))
    (throw (Error. (str "Actor has no parent window " actor)))))
