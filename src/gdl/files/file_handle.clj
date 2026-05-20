(ns gdl.files.file-handle
  (:refer-clojure :exclude [list]))

(defprotocol FileHandle
  (list [_])
  (path [_])
  (extension [_])
  (directory? [_])
  (texture [_]))
