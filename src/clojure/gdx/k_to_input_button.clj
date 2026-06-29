(ns clojure.gdx.k-to-input-button
  (:import (com.badlogic.gdx Input$Buttons)))

(defn f [k]
  (case k
    :input.buttons/left Input$Buttons/LEFT
    :input.buttons/right Input$Buttons/RIGHT))
