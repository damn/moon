(ns clojure.scenes.scene2d.group.add-actor
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn add-actor! [^Group group actor]
  (.addActor group actor))

(defn add-actors! [group actors]
  (run! #(add-actor! group %) actors))
