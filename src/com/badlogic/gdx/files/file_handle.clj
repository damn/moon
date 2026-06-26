(ns com.badlogic.gdx.files.file-handle
  (:import (com.badlogic.gdx.files FileHandle))
  (:refer-clojure :exclude [list]))

(defn type-hint
  ^FileHandle
  [obj]
  obj)

(defn list [^FileHandle file-handle]
  (.list file-handle))

(defn extension [^FileHandle file-handle]
  (.extension file-handle))

(defn path [^FileHandle file-handle]
  (.path file-handle))

(defn directory? [^FileHandle file-handle]
  (.isDirectory file-handle))
