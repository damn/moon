(ns gdl.actor.set-touchable
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-touchable! [^Actor actor touchable]
  (.setTouchable actor touchable))
