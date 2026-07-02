(ns clojure.gdx.files.internal
  (:import (com.badlogic.gdx Files)))

(defn f [files path]
  (Files/.internal files path))
