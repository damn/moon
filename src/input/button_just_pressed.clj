(ns input.button-just-pressed
  (:require [clojure.gdx.input$buttons.key-to-value :as key->value]
            [clojure.gdx.input.is-button-just-pressed :as is-button-just-pressed?]))

(defn f [input k]
  (is-button-just-pressed?/f input (key->value/f k)))
