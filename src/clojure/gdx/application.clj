(ns clojure.gdx.application
  (:require [clojure.gdx.graphics.g2d.freetype.font-generator :as font-generator]
            [clojure.gdx.graphics.g2d.freetype.font-generator.parameter :as parameter]
            [clojure.gdx.graphics.texture.filter :as texture.filter]
            clojure.graphics.freetype)
  (:import (com.badlogic.gdx Application)))

(extend-type Application
  clojure.graphics.freetype/Freetype
  (generate-font [_application file-handle {:keys [size]}]
    (let [generator (font-generator/create file-handle)
          font (font-generator/generate-font generator
                                             (doto (parameter/create)
                                               (parameter/set-size! size)
                                               (parameter/set-min-filter! texture.filter/linear)
                                               (parameter/set-mag-filter! texture.filter/linear)))]
      (.dispose generator)
      font)))
