(ns clojure.gdx.files.file-handle
  (:refer-clojure :exclude [list])
  (:import (com.badlogic.gdx.files FileHandle)))

(defn list [^FileHandle file-handle]
  (.list file-handle))

(defn directory? [^FileHandle file-handle]
  (.isDirectory file-handle))

(defn extension [^FileHandle file-handle]
  (.extension file-handle))

(defn path [^FileHandle file-handle]
  (.path file-handle))
