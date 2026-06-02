(ns clojure.gdx.scene2d.actor.find-ancestor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn find-ancestor [^Actor actor pred]
  (if-let [p (.getParent actor)]
    (if (pred p)
      p
      (find-ancestor p pred))
    (throw (Error. (str "Actor has no parent window " actor)))))
