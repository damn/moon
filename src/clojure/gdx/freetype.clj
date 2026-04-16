(ns clojure.gdx.freetype
  (:require [clj.api.com.badlogic.gdx.graphics.texture.filter :as texture.filter]
            [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator :as font-generator]
            [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator.parameter :as parameter]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable]))

(defn generate-font
  [_application
   file-handle
   {:keys [size]}]
  (let [generator (font-generator/create file-handle)
        font (font-generator/generate-font generator
                                           (doto (parameter/create)
                                             (parameter/set-size! size)
                                             (parameter/set-min-filter! texture.filter/linear)
                                             (parameter/set-mag-filter! texture.filter/linear)))]
    (disposable/dispose! generator)
    font))
