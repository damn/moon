(ns font-generator.generate-font
  (:require [com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator :as font-generator]))

(defn f [generator parameter]
  (font-generator/generate-font generator parameter))
