(ns clojure.gdx.graphics.g2d.bitmap-font
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]))

(defn draw! [& args]
  (apply bitmap-font/draw args))

(defn get-data [& args]
  (apply bitmap-font/get-data args))

(defn get-line-height [& args]
  (apply bitmap-font/get-line-height args))

(defn set-use-integer-positions! [& args]
  (apply bitmap-font/set-use-integer-positions args))
