(ns scene2d.group.find-actor
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]))

(defn find-actor [group name]
  (group/find-actor group name))
