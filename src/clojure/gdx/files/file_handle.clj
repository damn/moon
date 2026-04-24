(ns clojure.gdx.files.file-handle
  (:require clojure.files.file-handle)
  (:import (com.badlogic.gdx.files FileHandle)))

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
