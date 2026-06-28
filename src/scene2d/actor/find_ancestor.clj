(ns scene2d.actor.find-ancestor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn find-ancestor [actor pred?]
  (loop [a actor]
    (if-let [p (Actor/.getParent a)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))
