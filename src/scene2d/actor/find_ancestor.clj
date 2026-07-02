(ns scene2d.actor.find-ancestor
  (:require [clojure.gdx.actor.get-parent :as get-parent]))

(defn find-ancestor [actor pred?]
  (loop [a actor]
    (if-let [p (get-parent/f a)]
      (if (pred? p)
        p
        (recur p))
      (throw (Error. (str "Actor has no matching ancestor " actor))))))
