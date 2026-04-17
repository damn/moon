(ns clojure.graphics.freetype)

(defprotocol Freetype
  (generate-font [application file-handle params]))
