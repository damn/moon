(ns clojure.gdx.graphics.g2d.freetype.font-generator
  (:require [clojure.files.file-handle :as file-handle]
            [clojure.graphics.g2d.freetype.font-generator :as font-generator])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(extend-type FileHandle
  file-handle/FreetypeFontGenerator
  (freetype-font-generator [file-handle]
    (FreeTypeFontGenerator. file-handle)))

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
