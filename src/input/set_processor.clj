(ns input.set-processor
  (:import (com.badlogic.gdx Input)))

(defn f [input processor]
  (.setInputProcessor ^Input input processor))
