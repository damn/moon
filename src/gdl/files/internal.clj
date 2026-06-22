(ns gdl.files.internal
  (:import (com.badlogic.gdx Files)))

(defn f [^Files files path]
  (.internal files path))
