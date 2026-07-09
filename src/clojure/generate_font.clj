(ns clojure.generate-font
  (:require [com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator :as free-type-font-generator]))

(defn f [& args]
  (apply free-type-font-generator/generate-font args))
