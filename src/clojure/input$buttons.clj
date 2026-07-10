(ns clojure.input$buttons
  (:require [com.badlogic.gdx.input$buttons :as input-buttons]))

(defn key-to-value [k]
  (case k
    :input.buttons/left input-buttons/LEFT
    :input.buttons/right input-buttons/RIGHT))
