(ns clojure.file-handle
  (:require [com.badlogic.gdx.files.file-handle :as fh]))

(defn directory? [file-handle]
  (fh/directory? file-handle))

(defn extension [file-handle]
  (fh/extension file-handle))

(defn list-files [file-handle]
  (fh/list-files file-handle))

(defn path [file-handle]
  (fh/path file-handle))
