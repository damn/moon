(ns clojure.freetype)

(defprotocol Freetype
  (generate-font [application file-handle params]))
