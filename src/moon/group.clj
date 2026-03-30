(ns moon.group
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.group :as group]))

(defn add-actors! [group actors]
  (run! #(group/add-actor! group %) actors))
