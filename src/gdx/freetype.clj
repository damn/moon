(ns gdx.freetype
  (:require [com.badlogic.gdx.graphics.texture.filter :as texture.filter]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.enable-markup :as enable-markup]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.set-scale :as set-scale]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.set-use-integer-positions :as set-use-integer-positions]
            [com.badlogic.gdx.graphics.g2d.freetype.font-generator :as font-generator]
            [com.badlogic.gdx.graphics.g2d.freetype.font-generator.generate-font :as generate-font]
            [com.badlogic.gdx.graphics.g2d.freetype.font-generator.dispose :refer [dispose!]]
            [com.badlogic.gdx.graphics.g2d.freetype.font-generator.parameter :as parameter]))

(defn generate-font
  [file-handle
   {:keys [size
           quality-scaling
           use-integer-positions?]}]
  (let [generator (font-generator/create file-handle)
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
