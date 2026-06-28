(ns ctx.default-font
  (:require [files.internal :as internal]
            [gdx.graphics.texture.filter :as texture.filter]
            [bitmap-font-data.enable-markup :refer [enable-markup!]]
            [bitmap-font-data.set-scale :as set-scale]
            [file-handle.font-generator :as font-generator]
            [font-generator.generate-font :as generate-font]
            [freetype.font-generator-parameter :as parameter])
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont)
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
        generator (font-generator/f (internal/f files path))
        font (generate-font/f generator
                              (parameter/create
                               {:size (* size quality-scaling)
                                ; texture.filter/linear because scaling to world-units
                                :min-filter texture.filter/linear
                                :mag-filter texture.filter/linear}))]
    (Disposable/.dispose generator)
    (set-scale/f (.getData ^BitmapFont font) (/ quality-scaling))
    (enable-markup! (.getData ^BitmapFont font))
    (.setUseIntegerPositions ^BitmapFont font use-integer-positions?)
    font))
