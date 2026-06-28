(ns ctx.default-font
  (:require [files.internal :as internal]
            [gdx.graphics.texture.filter :as texture.filter]
            [bitmap-font.get-data :refer [get-data]]
            [bitmap-font-data.enable-markup :refer [enable-markup!]]
            [bitmap-font-data.set-scale :as set-scale]
            [bitmap-font.set-use-integer-positions :as set-use-integer-positions]
            [file-handle.font-generator :as font-generator]
            [font-generator.generate-font :as generate-font]
            [freetype.font-generator-parameter :as parameter])
  (:import (com.badlogic.gdx.utils Disposable)))

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
    (set-scale/f (get-data font) (/ quality-scaling))
    (enable-markup! (get-data font))
    (set-use-integer-positions/f! font use-integer-positions?)
    font))
