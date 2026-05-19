(ns gdl.graphics.g2d.freetype.font-generator)

(defprotocol FreeTypeFontGenerator
  (generate-font [_ parameter])
  (dispose! [_]))
