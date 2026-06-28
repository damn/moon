(ns ctx.default-font
  (:require [gdx.graphics.texture.filter :as texture.filter]
            [font-generator.generate-font :as generate-font]
            [freetype.font-generator-parameter :as parameter])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.graphics.g2d BitmapFont)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)
           (com.badlogic.gdx.utils Disposable)))

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
        font (generate-font/f generator
                              (parameter/create
                               {:size (* size quality-scaling)
                                ; texture.filter/linear because scaling to world-units
                                :min-filter texture.filter/linear
                                :mag-filter texture.filter/linear}))]
    (Disposable/.dispose generator)
    (.setScale (.getData ^BitmapFont font) (/ quality-scaling))
    (set! (.markupEnabled (.getData ^BitmapFont font)) true)
    (.setUseIntegerPositions ^BitmapFont font use-integer-positions?)
    font))
