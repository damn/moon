(ns com.badlogic.gdx.scenes.scene2d.touchable
  (:import (com.badlogic.gdx.scenes.scene2d Touchable)))

(defn k->value [k]
  (case k
    :touchable/disabled Touchable/disabled))
