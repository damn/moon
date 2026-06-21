(ns create.default-font
  (:require [clojure.files :as files]
            [clojure.texture.filter :as texture.filter]
            [clojure.bitmap-font.enable-markup :as enable-markup]
            [clojure.bitmap-font.set-scale :as set-scale]
            [clojure.bitmap-font.set-use-integer-positions :as set-use-integer-positions]
            [clojure.font-generator :as font-generator]
            [clojure.font-generator.generate-font :as generate-font]
            [clojure.font-generator.dispose :refer [dispose!]]
            [clojure.font-generator.parameter :as parameter]))

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
