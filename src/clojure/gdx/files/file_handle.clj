(ns clojure.gdx.files.file-handle
  (:require [clojure.files.file-handle :as file-handle])
  (:import (com.badlogic.gdx.files FileHandle)))

(extend-type FileHandle
  file-handle/FileHandle
  (list [this]
    (.list this))
  (path [this]
    (.path this))
  (extension [this]
    (.extension this))
  (directory? [this]
    (.isDirectory this)))
