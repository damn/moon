(ns gdx.files
  (:import (com.badlogic.gdx Files)))

(defn internal [files path]
  (.internal ^Files files path))
