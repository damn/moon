(ns clojure.java.io.file
  (:import (java.io File)))

(defn list-files [file]
  (File/.listFiles file))

(defn file? [file]
  (File/.isFile file))

(defn get-name [file]
  (File/.getName file))
