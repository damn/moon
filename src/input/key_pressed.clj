(ns input.key-pressed
  (:require [clojure.gdx.input$keys.key-to-value :as key->value]
            [clojure.gdx.input.is-key-pressed :as is-key-pressed?]))

(defn f [input k]
  (is-key-pressed?/f input (key->value/f k)))
