(ns clojure.graphics.g2d.freetype.font-generator)

(defprotocol FreeTypeFontGenerator
  (generate-font [_ parameters])
  (dispose! [_]))
