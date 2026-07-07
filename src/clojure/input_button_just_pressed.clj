(ns clojure.input-button-just-pressed
  (:require [clojure.input :as input]
            [clojure.input$buttons :as input-buttons]))

(defn f [input k]
  (input/button-just-pressed? input (input-buttons/key-to-value k)))
