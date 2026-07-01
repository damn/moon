(ns input.key-pressed
  (:require [clojure.gdx.input$keys.key-to-value :as key->value])
  (:import (com.badlogic.gdx Input)))

(defn f [input k]
  (.isKeyPressed ^Input input (key->value/f k)))
