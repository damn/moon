(ns clojure.gdx.input.set-input-processor!
  (:import (com.badlogic.gdx Input)))

(defn f [input processor]
  (.setInputProcessor ^Input input processor))
