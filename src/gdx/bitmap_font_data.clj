(ns gdx.bitmap-font-data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn scale-x [font-data]
  (.scaleX ^BitmapFont$BitmapFontData font-data))

(defn set-scale! [font-data scale]
  (.setScale ^BitmapFont$BitmapFontData font-data scale))

(defn set-markup-enabled! [font-data enabled?]
  (set! (.markupEnabled ^BitmapFont$BitmapFontData font-data) enabled?))
