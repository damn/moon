(ns clojure.gdx.bitmap-font$bitmap-font-data.set-markup-enabled
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn f [^BitmapFont$BitmapFontData data enabled?]
  (set! (.markupEnabled data) enabled?))
