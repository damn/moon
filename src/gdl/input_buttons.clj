(ns gdl.input-buttons
  (:import (com.badlogic.gdx Input$Buttons)))

(defn k->value [k]
  (case k
    :input.buttons/left  Input$Buttons/LEFT
    :input.buttons/right Input$Buttons/RIGHT))
