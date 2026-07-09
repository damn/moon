(ns clojure.free-type-font-generator
  (:require [com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator :as free-type-font-generator]))

(defn f [& args]
  (apply free-type-font-generator/f args))
