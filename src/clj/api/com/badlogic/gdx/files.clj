(ns clj.api.com.badlogic.gdx.files
  (:import (com.badlogic.gdx Files)))

(defn internal [^Files files path]
  (.internal files path))
