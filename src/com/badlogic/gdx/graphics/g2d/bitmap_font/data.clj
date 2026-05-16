(ns com.badlogic.gdx.graphics.g2d.bitmap-font.data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn scale-x [^BitmapFont$BitmapFontData data]
  (.scaleX data))

(defn set-scale! [^BitmapFont$BitmapFontData data scale]
  (.setScale data scale))

(defn set-markup-enabled! [^BitmapFont$BitmapFontData data enabled?]
  (set! (.markupEnabled data) enabled?))
