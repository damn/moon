(ns gdx.input.key-just-pressed
  (:require [clojure.input :as input]
            [clojure.input$keys :as input-keys]))

(defn f [input k]
  (input/key-just-pressed? input (input-keys/key-to-value k)))
