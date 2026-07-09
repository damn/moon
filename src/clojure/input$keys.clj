(ns clojure.input$keys
  (:require [com.badlogic.gdx.input$keys :as input-keys]))

(defn key-to-value [& args]
  (apply input-keys/key-to-value args))
