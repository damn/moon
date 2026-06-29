(ns input.key-pressed
  (:require [clojure.gdx.k-to-input-key :as k-to-input-key])
  (:import (com.badlogic.gdx Input)))

(defn f [input k]
  (.isKeyPressed ^Input input (k-to-input-key/f k)))
