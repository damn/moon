(ns input.key-just-pressed
  (:require [clojure.gdx.k-to-input-key :as k-to-input-key])
  (:import (com.badlogic.gdx Input)))

(defn f [input k]
  (.isKeyJustPressed ^Input input (k-to-input-key/f k)))
