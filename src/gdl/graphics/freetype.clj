(ns gdl.graphics.freetype
  (:require [clj.api.com.badlogic.gdx.graphics.texture.filter :as texture.filter]
            [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator :as font-generator]
            [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator.parameter :as parameter]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable]
            [gdl.graphics.bitmap-font :as font]))

(defn generate-font*
  [file-handle
   {:keys [size min-filter mag-filter]}]
  (let [generator (font-generator/create file-handle)
        font (font-generator/generate-font generator
                                           (doto (parameter/create)
                                             (parameter/set-size! size)
                                             (parameter/set-min-filter! min-filter)
                                             (parameter/set-mag-filter! mag-filter)))]
    (disposable/dispose! generator)
    font))

(defn generate-font
  [file-handle {:keys [size
                       quality-scaling
                       enable-markup?
                       use-integer-positions?]}]
  (doto (generate-font* file-handle
                        {:size (* size quality-scaling)
                         :min-filter texture.filter/linear
                         :mag-filter texture.filter/linear})
    (font/set-scale! (/ quality-scaling))
    (font/enable-markup! enable-markup?)
    (font/use-integer-positions! use-integer-positions?)))
