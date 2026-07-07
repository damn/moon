(ns clojure.input$buttons
  (:import (com.badlogic.gdx Input$Buttons)))

(defn key-to-value [k]
  (case k
    :input.buttons/left Input$Buttons/LEFT
    :input.buttons/right Input$Buttons/RIGHT))
