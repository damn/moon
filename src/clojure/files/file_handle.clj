(ns clojure.files.file-handle
  (:refer-clojure :exclude [list]))

(defprotocol FileHandle
  (list [_])
  (path [_])
  (extension [_])
  (directory? [_]))

(defprotocol Pixmap
  (pixmap [_]))

(defprotocol Skin
  (skin [_]))
