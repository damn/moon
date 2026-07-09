(ns com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator
  (:import
           (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           ))

(defn f [^FileHandle file-handle]
  (FreeTypeFontGenerator. file-handle))

(defn generate-font [generator ^FreeTypeFontGenerator$FreeTypeFontParameter parameter]
  (FreeTypeFontGenerator/.generateFont generator parameter))
