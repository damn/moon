(ns com.badlogic.gdx.graphics.pixmap
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap Pixmap$Format)
           ))

(defn new
  ([^FileHandle file-handle]
   (Pixmap. file-handle))
  ([width height ^Pixmap$Format format]
   (Pixmap. (int width) (int height) format)))

(defn set-color! [^Pixmap pixmap r g b a]
  (Pixmap/.setColor pixmap r g b a))

(defn draw-pixel! [^Pixmap pixmap x y]
  (Pixmap/.drawPixel pixmap (int x) (int y)))
