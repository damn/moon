(ns clojure.gdx.files
  (:require clojure.files
            clojure.files.file-handle)
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.files FileHandle)))

(extend-type Files
  clojure.files/Files
  (internal [this path]
    (.internal this path)))

(extend-type FileHandle
  clojure.files.file-handle/FileHandle
  (list [this]
    (.list this))

  (directory? [this]
    (.isDirectory this))

  (extension [this]
    (.extension this))

  (path [this]
    (.path this)))
