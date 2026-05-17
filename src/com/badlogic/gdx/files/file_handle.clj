(ns com.badlogic.gdx.files.file-handle
  (:refer-clojure :exclude [list])
  (:import (com.badlogic.gdx.files FileHandle)))

(defn list [^FileHandle file]
  (.list file))

(defn directory? [^FileHandle file]
  (.isDirectory file))

(def extension FileHandle/.extension)
(def path FileHandle/.path)
