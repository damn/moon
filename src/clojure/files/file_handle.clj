(ns clojure.files.file-handle
  (:refer-clojure :exclude [list]))

(defprotocol FileHandle
  (list [_])
  (directory? [_])
  (extension [_])
  (path [_]))
