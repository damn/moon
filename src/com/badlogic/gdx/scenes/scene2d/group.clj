(ns com.badlogic.gdx.scenes.scene2d.group
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.scenes.scene2d Actor Group)
           ))

(defn add-actor! [^Group group ^Actor actor]
  (Group/.addActor group actor))

(defn clear-children! [^Group group]
  (Group/.clearChildren group))

(defn find-actor [^Group group name]
  (Group/.findActor group name))

(defn get-children [^Group group]
  (Group/.getChildren group))

(defn new []
  (Group.))
