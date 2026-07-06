(ns gdx.input.button-just-pressed
  (:require [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.input$buttons :as input-buttons]))

(defn f [input k]
  (input/button-just-pressed? input (input-buttons/key-to-value k)))
