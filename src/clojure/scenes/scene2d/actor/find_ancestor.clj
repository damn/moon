(ns clojure.scenes.scene2d.actor.find-ancestor
  (:require [clojure.scenes.scene2d.actor.get-parent :refer [get-parent]]))

(defn find-ancestor [actor pred]
  (if-let [p (get-parent actor)]
    (if (pred p)
      p
      (find-ancestor p pred))
    (throw (Error. (str "Actor has no parent window " actor)))))
