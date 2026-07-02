(ns clojure.gdx.actor.add-listener
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [actor listener]
  (Actor/.addListener actor listener))
