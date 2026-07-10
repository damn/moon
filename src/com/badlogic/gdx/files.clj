(ns com.badlogic.gdx.files
  (:import (com.badlogic.gdx Files)))

(defn internal [files path]
  (.internal ^Internal files path))
