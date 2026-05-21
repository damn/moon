(ns com.badlogic.gdx.files
  (:require [clojure.files :as files])
  (:import (com.badlogic.gdx Files)))

(extend-type Files
  files/Files
  (internal [this path]
    (.internal this path)))
