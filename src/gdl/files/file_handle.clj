(ns gdl.files.file-handle
  (:refer-clojure :exclude [list])
  (:import (com.badlogic.gdx.files FileHandle)))

(def list FileHandle/.list)
(def directory? FileHandle/.isDirectory)
(def extension FileHandle/.extension)
(def path FileHandle/.path)
