(ns clojure.gdx.actor.set-touchable
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Touchable)))

(defn f [^Actor actor touchable]
  (Actor/.setTouchable actor touchable))
