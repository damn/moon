(ns com.badlogic.gdx.graphics.g2d.bitmap-font.data.enable-markup
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn enable-markup! [^BitmapFont$BitmapFontData data]
  (set! (.markupEnabled data) true))
