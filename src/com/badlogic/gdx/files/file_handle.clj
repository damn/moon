(ns com.badlogic.gdx.files.file-handle
  (:refer-clojure :exclude [list])
  (:import (com.badlogic.gdx.files FileHandle)))

(defn isDirectory [file-handle]
  (.isDirectory ^FileHandle file-handle))

(defn extension [file-handle]
  (.extension ^FileHandle file-handle))

(defn list [file-handle]
  (.list ^FileHandle file-handle))

(defn path [file-handle]
  (.path ^FileHandle file-handle))
