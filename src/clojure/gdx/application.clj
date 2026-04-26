(ns clojure.gdx.application
  (:require clojure.graphics.freetype)
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(extend-type Application
  clojure.graphics.freetype/Freetype
  (generate-font [_application file-handle {:keys [size]}]
    (let [generator (FreeTypeFontGenerator. file-handle)
          font (.generateFont generator
                              (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                                (set! (.size params) size)
                                (set! (.minFilter params) Texture$TextureFilter/Linear)
                                (set! (.magFilter params) Texture$TextureFilter/Linear)
                                params))]
      (.dispose generator)
      font)))
