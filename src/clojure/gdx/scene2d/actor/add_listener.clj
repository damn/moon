(ns clojure.gdx.scene2d.actor.add-listener
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn add-listener! [actor listener]
  (Actor/.addListener actor listener))
