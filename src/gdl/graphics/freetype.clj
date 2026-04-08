(ns gdl.graphics.freetype
  (:require [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator :as font-generator]
            [clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator.parameter :as parameter]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable]))

(defn generate-font
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
