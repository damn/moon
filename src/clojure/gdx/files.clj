(ns clojure.gdx.files
  (:require clojure.files)
  (:import (com.badlogic.gdx Files)))

(extend-type Files
  clojure.files/Files
  (internal [this path]
    (.internal this path)))
