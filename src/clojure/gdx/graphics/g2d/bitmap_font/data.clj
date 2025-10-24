(ns clojure.gdx.graphics.g2d.bitmap-font.data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn scale-x [^BitmapFont$BitmapFontData data]
  (.scaleX data))

(defn set-scale! [^BitmapFont$BitmapFontData data scale]
  (.setScale data scale))

(defn set-enable-markup! [^BitmapFont$BitmapFontData data enable-markup?]
  (set! (.markupEnabled data) enable-markup?))
