(ns clojure.input$buttons
  (:require [com.badlogic.gdx.input$buttons :as input-buttons]))

(defn key-to-value [& args]
  (apply input-buttons/key-to-value args))
