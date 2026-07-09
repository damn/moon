(ns clojure.input.key-pressed
  (:require [clojure.input :as input]
            [clojure.input$keys :as input-keys]))

(defn f [input k]
  (input/key-pressed? input (input-keys/key-to-value k)))
