(ns input.key-just-pressed
  (:require [clojure.gdx.input$keys.key-to-value :as key->value]
            [clojure.gdx.input.is-key-just-pressed :as is-key-just-pressed?]))

(defn f [input k]
  (is-key-just-pressed?/f input (key->value/f k)))
