(ns moon.create.default-font
  (:require [gdl.files :as files]
            [gdl.graphics.bitmap-font :as bitmap-font]
            [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator :as font-generator]
            [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator.parameter :as parameter]
            [clj.api.com.badlogic.gdx.graphics.texture.filter :as texture.filter]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable]))

(defn- generate-font
  [file-handle {:keys [size
                       quality-scaling
                       enable-markup?
                       use-integer-positions?]}]
  (let [generator (font-generator/create file-handle)
        font (font-generator/generate-font generator
                                           (doto (parameter/create)
                                             (parameter/set-size! (* size quality-scaling))
                                             (parameter/set-min-filter! texture.filter/linear)
                                             (parameter/set-mag-filter! texture.filter/linear)))]
    (disposable/dispose! generator)
    (bitmap-font/set-scale! font (/ quality-scaling))
    (bitmap-font/enable-markup! font enable-markup?)
    (bitmap-font/use-integer-positions! font use-integer-positions?)
    font))

(defn do!
  [{:keys [ctx/files]
    :as ctx}
   {:keys [path params]}]
  (assoc ctx :ctx/default-font (generate-font (files/internal files path)
                                              params)))
