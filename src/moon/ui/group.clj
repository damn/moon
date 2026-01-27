(ns moon.ui.group
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn add-actors! [^Group group actors]
  (run! #(.addActor group %) actors))
