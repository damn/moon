(ns gdx.input.buttons
  (:require [com.badlogic.gdx.input$buttons :as buttons]))

(defn k->value [k]
  (case k
    :input.buttons/left buttons/left
    :input.buttons/right buttons/right))
