(ns clojure.gdx.scene2d.actor.set-touchable
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-touchable! [actor touchable]
  (Actor/.setTouchable actor touchable))
