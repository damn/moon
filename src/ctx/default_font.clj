(ns ctx.default-font
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn step
  [{:keys [ctx/files]}]
  (let [{:keys [path
                size
                quality-scaling
                use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                          :size 16
                                          :quality-scaling 2
                                          :use-integer-positions? false}
        generator (FreeTypeFontGenerator. (Files/.internal files path))
        font (.generateFont generator
                            (let [parameter (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                              (set! (.size parameter) (* size quality-scaling))
                              (set! (.minFilter parameter) Texture$TextureFilter/Linear)
                              (set! (.magFilter parameter) Texture$TextureFilter/Linear)
                              parameter))]
    (.dispose generator)
    (.setScale (.getData font) (/ quality-scaling))
    (set! (.markupEnabled (.getData font)) true)
    (.setUseIntegerPositions font use-integer-positions?)
    font))
