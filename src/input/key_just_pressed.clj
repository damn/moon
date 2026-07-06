(ns input.key-just-pressed
  (:require [com.badlogic.gdx.input$keys :as input-keys]
            [clojure.gdx.input.is-key-just-pressed :as is-key-just-pressed?]))

(defn f [input k]
  (is-key-just-pressed?/f input (input-keys/key-to-value k)))
