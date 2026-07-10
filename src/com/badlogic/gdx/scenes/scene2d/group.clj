(ns com.badlogic.gdx.scenes.scene2d.group
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)))

(defn new []
  (Group.))

(defn addActor [^Group group ^Actor actor]
  (.addActor group actor))

(defn clearChildren [^Group group]
  (.clearChildren group))

(defn findActor [^Group group name]
  (.findActor group name))

(defn getChildren [^Group group]
  (.getChildren group))
