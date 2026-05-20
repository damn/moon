(ns com.badlogic.gdx.files
  (:require [gdl.files :as files])
  (:import (com.badlogic.gdx Files)))

(extend-type Files
  files/Files
  (internal [this path]
    (.internal this path)))
