(ns clojure.gdx.input.is-button-just-pressed
  (:import (com.badlogic.gdx Input)))

(defn f [input button-code]
  (Input/.isButtonJustPressed input button-code))
