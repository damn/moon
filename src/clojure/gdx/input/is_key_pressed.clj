(ns clojure.gdx.input.is-key-pressed
  (:import (com.badlogic.gdx Input)))

(defn f [input key-code]
  (Input/.isKeyPressed input key-code))
