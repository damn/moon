(ns clojure.com.badlogic.gdx.files
  "Provides standard access to the filesystem, classpath, Android app storage (internal and external), and Android assets
  directory."
  (:import (com.badlogic.gdx Files)))

(defn internal
  "Convenience method that returns a internal file handle."
  [^Files files path]
  (.internal files path))
