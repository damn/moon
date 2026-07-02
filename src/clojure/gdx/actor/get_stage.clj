(ns clojure.gdx.actor.get-stage
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [actor]
  (Actor/.getStage actor))
