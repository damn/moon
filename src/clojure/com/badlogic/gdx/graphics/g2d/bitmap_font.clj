(ns clojure.com.badlogic.gdx.graphics.g2d.bitmap-font
  "Renders bitmap fonts. The font consists of 2 files: an image file or [[clojure.com.badlogic.gdx.graphics.g2d.texture-region]] containing the glyphs and a file in
  the AngleCode BMFont text format that describes where each glyph is on the image.
  
  Text is drawn using a [[clojure.com.badlogic.gdx.graphics.g2d.batch]]. Text can be cached in a bitmap font cache for faster rendering of static text, which
  saves needing to compute the location of each glyph each frame.
  
  * The texture for a BitmapFont loaded from a file is managed. [clojure.com.badlogic.gdx.utils.disposable/dispose!] must be called to free the texture when no
  longer needed. A BitmapFont loaded using a [[clojure.com.badlogic.gdx.graphics.g2d.texture-region]] is managed if the region's texture is managed. Disposing the
  BitmapFont disposes the region's texture, which may not be desirable if the texture is still being used elsewhere.
  
  The code was originally based on Matthias Mann's TWL BitmapFont class. Thanks for sharing, Matthias! :)"
  (:import (com.badlogic.gdx.graphics.g2d Batch BitmapFont)))

(defn data
  "Gets the underlying [[clojure.com.badlogic.gdx.graphics.g2d.bitmap-font-bitmap-font-data]] for this BitmapFont."
  [^BitmapFont font]
  (.getData font))

(defn draw
  "Draws text at the specified position."
  [^BitmapFont font ^Batch batch text x y target-width align wrap?]
  (.draw font batch text (float x) (float y) (float target-width) align wrap?))

(defn line-height
  "Returns the line height, which is the distance from one line of text to the next."
  [^BitmapFont font]
  (.getLineHeight font))

(defn set-use-integer-positions!
  "Specifies whether to use integer positions. Default is to use them so filtering doesn't kick in as badly."
  [^BitmapFont font v]
  (.setUseIntegerPositions font v))
