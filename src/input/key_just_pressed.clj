(ns input.key-just-pressed
  (:require [clojure.gdx.input$keys.key-to-value :as key->value])
  (:import (com.badlogic.gdx Input)))

(defn f [input k]
  (.isKeyJustPressed ^Input input (key->value/f k)))
