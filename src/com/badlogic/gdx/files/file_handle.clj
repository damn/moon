(ns com.badlogic.gdx.files.file-handle
  (:import (com.badlogic.gdx.files FileHandle)))

(defn directory? [^FileHandle file-handle]
  (.isDirectory file-handle))

(defn extension [^FileHandle file-handle]
  (.extension file-handle))

(defn list-files [^FileHandle file-handle]
  (.list file-handle))

(defn path [^FileHandle file-handle]
  (.path file-handle))
