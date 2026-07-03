(ns clojure.gdx.input.is-key-just-pressed
  (:import (com.badlogic.gdx Input)))

(defn f [input key-code]
  (Input/.isKeyJustPressed input key-code))
