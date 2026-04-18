(ns clojure.gdx.input.buttons
  (:import (com.badlogic.gdx Input$Buttons)))

(defn k->value [b]
  (case b
    :input.buttons/left  Input$Buttons/LEFT
    :input.buttons/right Input$Buttons/RIGHT
    ))
