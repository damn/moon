(ns clojure.gdx
  (:require clojure.files
            clojure.files.file-handle
            )
  (:import (com.badlogic.gdx Files
                             Gdx)
           (com.badlogic.gdx.files FileHandle)
           ))

(defn app []
  Gdx/app)

(defn audio []
  Gdx/audio)

(defn graphics []
  Gdx/graphics)

(defn files []
  Gdx/files)

(defn input []
  Gdx/input)

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
