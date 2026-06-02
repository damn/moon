(ns clojure.gdx.scene2d.actor.find-ancestor
  (:require [clojure.gdx.scene2d.actor.parent :refer [actor-parent]]))

(defn find-ancestor [actor pred]
  (if-let [p (actor-parent actor)]
    (if (pred p)
      p
      (find-ancestor p pred))
    (throw (Error. (str "Actor has no parent window " actor)))))
