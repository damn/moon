(ns clojure.com.badlogic.gdx.graphics.pixmap
  "A Pixmap represents an image in memory. It has a width and height expressed in pixels as well as a [[clojure.com.badlogic.gdx.graphics.pixmap-format]] specifying
  the number and order of color components per pixel. Coordinates of pixels are specified with respect to the top left corner of
  the image, with the x-axis pointing to the right and the y-axis pointing downwards.
  
  By default all methods use blending. You can disable blending with set blending, which may reduce
  blitting time by ~30%. The draw pixmap method will scale and
  stretch the source image to a target image. There either nearest neighbour or bilinear filtering can be used.
  
  A Pixmap stores its data in native heap memory. It is mandatory to call [clojure.com.badlogic.gdx.utils.disposable/dispose!] when the pixmap is no longer
  needed, otherwise memory leaks will result"
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap Pixmap$Format)))

(defn create
  "Creates a new Pixmap instance from the given encoded image data. The image can be encoded as JPEG, PNG or BMP. Not
  available on GWT backend.
  
  Offset is based on the position of the buffer. Length is based on the remaining bytes of the buffer."
  ([^FileHandle file-handle]
   (Pixmap. file-handle))
  ([width height ^Pixmap$Format pixmap-format]
   (Pixmap. (int width) (int height) pixmap-format)))

(defn set-color!
  "Sets the color for the following drawing operations.
  
  - `r`: The red component.
  
  - `g`: The green component.
  
  - `b`: The blue component.
  
  - `a`: The alpha component."
  [^Pixmap pixmap r g b a]
  (.setColor pixmap (float r) (float g) (float b) (float a)))

(defn draw-pixel!
  "Draws a pixel at the given location with the current color.
  
  - `x`: the x-coordinate
  
  - `y`: the y-coordinate"
  [^Pixmap pixmap x y]
  (.drawPixel pixmap (int x) (int y)))
