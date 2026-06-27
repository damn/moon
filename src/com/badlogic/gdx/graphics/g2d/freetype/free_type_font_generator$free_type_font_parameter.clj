(ns com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator$free-type-font-parameter
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn create []
  (FreeTypeFontGenerator$FreeTypeFontParameter.))

(defn set-size! [^FreeTypeFontGenerator$FreeTypeFontParameter params size]
  (set! (.size params) size))

(defn set-min-filter! [^FreeTypeFontGenerator$FreeTypeFontParameter params min-filter]
  (set! (.minFilter params) min-filter))

(defn set-mag-filter! [^FreeTypeFontGenerator$FreeTypeFontParameter params mag-filter]
  (set! (.magFilter params) mag-filter))

(defn type-hint
  ^FreeTypeFontGenerator$FreeTypeFontParameter
  [obj]
  obj)
