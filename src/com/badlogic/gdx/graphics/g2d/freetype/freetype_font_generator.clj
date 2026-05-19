(ns com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator
  (:require [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator.parameter :as parameter]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator])
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)))

(defn create [file-handle]
  (FreeTypeFontGenerator. file-handle))

(extend-type FreeTypeFontGenerator
  font-generator/FreeTypeFontGenerator
  (generate-font [generator parameter]
    (.generateFont generator (parameter/create parameter)))

  (dispose! [generator]
    (.dispose generator)))
