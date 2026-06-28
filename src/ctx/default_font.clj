(ns ctx.default-font
  (:require [gdx.graphics.texture.filter :as texture.filter])
  (:import (com.badlogic.gdx Files)
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
                              (set! (.minFilter parameter) texture.filter/linear)
                              (set! (.magFilter parameter) texture.filter/linear)
                              parameter))]
    (.dispose generator)
    (.setScale (.getData font) (/ quality-scaling))
    (set! (.markupEnabled (.getData font)) true)
    (.setUseIntegerPositions font use-integer-positions?)
    font))
