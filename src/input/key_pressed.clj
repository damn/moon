(ns input.key-pressed
  (:require [com.badlogic.gdx.input$keys :as input-keys]
            [clojure.gdx.input.is-key-pressed :as is-key-pressed?]))

(defn f [input k]
  (is-key-pressed?/f input (input-keys/key-to-value k)))
