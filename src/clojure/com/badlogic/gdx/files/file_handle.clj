(ns clojure.com.badlogic.gdx.files.file-handle
  (:import (com.badlogic.gdx.files FileHandle))
  (:refer-clojure :exclude [list]))

(defn list [^FileHandle handle]
  (.list handle))

(defn directory? [^FileHandle handle]
  (.isDirectory handle))

(defn extension [^FileHandle handle]
  (.extension handle))

(defn path [^FileHandle handle]
  (.path handle))
