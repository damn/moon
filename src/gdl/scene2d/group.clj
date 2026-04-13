(ns gdl.scene2d.group
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]))

(def add-actor! group/add-actor!)
(def children group/children)
(def find-actor group/find-actor)
(def clear-children! group/clear-children!)

(defn add-actors! [group actors]
  (run! #(add-actor! group %) actors))
