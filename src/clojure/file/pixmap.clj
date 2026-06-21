(ns clojure.file.pixmap
  "Creates a new Pixmap instance from the given file. The file must be a Png, Jpeg or Bitmap. Paletted formats are not supported."
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap)))

(defn f
  [^FileHandle file-handle]
  (Pixmap. file-handle))
