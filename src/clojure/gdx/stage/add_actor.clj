(ns clojure.gdx.stage.add-actor
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn f [stage actor]
  (Stage/.addActor stage actor))
