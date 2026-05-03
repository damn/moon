(ns com.badlogic.gdx.files.file-handle
  (:refer-clojure :exclude [list])
  (:import (com.badlogic.gdx.files FileHandle)))

(defn list [^FileHandle file-handle]
  (.list file-handle))

(def directory? FileHandle/.isDirectory)
(def extension FileHandle/.extension)
(def path FileHandle/.path)
