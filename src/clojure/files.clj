(ns clojure.files
  (:import (com.badlogic.gdx Files)))

(defn internal [files path]
  (Files/.internal files path))
