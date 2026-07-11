(ns moon.actor
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn find-ancestor [actor pred?]
  (loop [a actor]
    (if-let [p (actor/getParent a)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))
