(ns create.default-font
  (:require [clojure.files :as files]
            [clojure.graphics.texture.filter :as texture.filter]
            [clojure.graphics.g2d.bitmap-font.enable-markup :as enable-markup]
            [clojure.graphics.g2d.bitmap-font.set-scale :as set-scale]
            [clojure.graphics.g2d.bitmap-font.set-use-integer-positions :as set-use-integer-positions]
            [clojure.graphics.g2d.freetype.font-generator :as font-generator]
            [clojure.graphics.g2d.freetype.font-generator.generate-font :as generate-font]
            [clojure.graphics.g2d.freetype.font-generator.dispose :refer [dispose!]]
            [clojure.graphics.g2d.freetype.font-generator.parameter :as parameter]))

(defn step
  [{:keys [ctx/files]}]
  (let [{:keys [path
                size
                quality-scaling
                use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                          :size 16
                                          :quality-scaling 2
                                          :use-integer-positions? false}
        generator (font-generator/create (files/internal files path))
        font (generate-font/f generator
                              (parameter/create
                               {:size (* size quality-scaling)
                                ; texture.filter/linear because scaling to world-units
                                :min-filter texture.filter/linear
                                :mag-filter texture.filter/linear}))]
    (dispose! generator)
    (set-scale/f! font (/ quality-scaling))
    (enable-markup/f! font)
    (set-use-integer-positions/f! font use-integer-positions?)
    font))
