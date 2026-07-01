(ns input.button-just-pressed
  (:require [clojure.gdx.input$buttons.key-to-value :as key->value])
  (:import (com.badlogic.gdx Input)))

(defn f [input k]
  (.isButtonJustPressed ^Input input (key->value/f k)))
