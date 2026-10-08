(ns files.create
  (:import (com.badlogic.gdx Files)))

(defn create-sound-file-handles! [^Files files paths]
  (into {}
        (for [path paths]
          [path (.internal files path)])))
