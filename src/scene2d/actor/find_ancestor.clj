(ns scene2d.actor.find-ancestor
  (:require [clojure.gdx :as gdx]))

(defn find-ancestor [actor pred?]
  (loop [a actor]
    (if-let [p (gdx/get-parent a)]
      (if (pred? p)
        p
        (recur p))
      nil)))
