(ns input.button-just-pressed
  (:require [com.badlogic.gdx.input$buttons :as input-buttons]
            [clojure.gdx.input.is-button-just-pressed :as is-button-just-pressed?]))

(defn f [input k]
  (is-button-just-pressed?/f input (input-buttons/key-to-value k)))
