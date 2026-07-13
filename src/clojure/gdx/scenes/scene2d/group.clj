(ns clojure.gdx.scenes.scene2d.group
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]))

(defn find-actor [group actor-name]
  (group/findActor group actor-name))

(defn add-actor! [group actor]
  (group/addActor group actor))
