(ns gdl.files.file-handle
  (:refer-clojure :exclude [list]))

(defprotocol FileHandle
  (list [_])
  (path [_])
  (extension [_])
  (directory? [_]))

(defprotocol Texture
  (texture [_]))

(defprotocol Pixmap
  (pixmap [_]))

(defprotocol Skin
  (skin [_]))

(defprotocol FreetypeFontGenerator
  (freetype-font-generator [_]))
