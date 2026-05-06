(ns com.badlogic.gdx.graphics.g2d.bitmap-font.data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn enable-markup! [^BitmapFont$BitmapFontData data enable-markup?]
  (set! (.markupEnabled data) enable-markup?))

(defn set-scale! [^BitmapFont$BitmapFontData data scale]
  (.setScale data scale))

(defn scale-x [^BitmapFont$BitmapFontData data]
  (.scaleX data))
