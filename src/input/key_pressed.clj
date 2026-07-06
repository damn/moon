(ns input.key-pressed
  (:require
            [com.badlogic.gdx.input :as input] [com.badlogic.gdx.input$keys :as input-keys]))

(defn f [input k]
  (input/is-key-pressed input (input-keys/key-to-value k)))
