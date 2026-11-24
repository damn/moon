(ns clojure.com.badlogic.gdx.files.file-handle
  "Represents a file or directory on the filesystem, classpath, Android app storage, or Android assets directory. FileHandles are
  created via a [[clojure.com.badlogic.gdx.files]] instance.
  
  Because some of the file types are backed by composite files and may be compressed (for example, if they are in an Android .apk
  or are found via the classpath), the methods for extracting a [path] or file may not be appropriate for all
  types. Use the Reader or Stream methods here to hide these dependencies from your platform independent code."
  (:import (com.badlogic.gdx.files FileHandle))
  (:refer-clojure :exclude [list]))

(defn list
  "Returns the paths to the children of this directory. Returns an empty list if this file handle represents a file and not a
  directory. On the desktop, an internal handle to a directory on the classpath will return a zero length
  array."
  [^FileHandle handle]
  (.list handle))

(defn directory?
  "Returns true if this file is a directory. Always returns false for classpath files. On Android, an
  internal handle to an empty directory will return false. On the desktop, an internal
  handle to a directory on the classpath will return false."
  [^FileHandle handle]
  (.isDirectory handle))

(defn extension
  "Returns the file extension (without the dot) or an empty string if the file name doesn't contain a dot."
  [^FileHandle handle]
  (.extension handle))

(defn path
  "Returns the path of the file as specified on construction, e.g. Gdx.files.internal(\"dir/file.png\") -> dir/file.png.
   backward slashes will be replaced by forward slashes."
  [^FileHandle handle]
  (.path handle))
