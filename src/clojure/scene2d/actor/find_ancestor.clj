(ns clojure.scene2d.actor.find-ancestor
  (:require
            [clojure.scene2d.actor.get-parent]))

(defn find-ancestor [actor pred?]
  (loop [a actor]
    (if-let [p (clojure.scene2d.actor.get-parent/f a)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))
