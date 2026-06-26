(ns scene2d.group.children
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]))

(defn children [group]
  (group/get-children group))
