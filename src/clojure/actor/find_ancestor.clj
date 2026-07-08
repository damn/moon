(ns clojure.actor.find-ancestor
  (:require
            [clojure.actor.get-parent]))

(defn find-ancestor [actor pred?]
  (loop [a actor]
    (if-let [p (clojure.actor.get-parent/f a)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))
