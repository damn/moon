(ns com.badlogic.gdx.graphics.g2d.freetype.font-generator
  (:require [gdl.graphics.g2d.freetype.font-generator :as font-generator])
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(extend-type FreeTypeFontGenerator
  font-generator/FreeTypeFontGenerator
  (generate-font [this {:keys [size
                               min-filter
                               mag-filter]}]
    (.generateFont this (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                          (set! (.size params) size)
                          (set! (.minFilter params) min-filter)
                          (set! (.magFilter params) mag-filter)
                          params)))

  (dispose! [this]
    (.dispose this)))
