(ns input.button-just-pressed
  (:require [clojure.gdx.k-to-input-button :as k-to-input-button])
  (:import (com.badlogic.gdx Input)))

(defn f [input k]
  (.isButtonJustPressed ^Input input (k-to-input-button/f k)))
