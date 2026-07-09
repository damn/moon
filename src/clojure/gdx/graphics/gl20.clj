(ns clojure.gdx.graphics.gl20
  (:require [com.badlogic.gdx.graphics.gl20 :as gl20]))

(def color-buffer-bit
  gl20/color-buffer-bit)

(defn clear! [& args]
  (apply gl20/clear! args))

(defn clear-color! [& args]
  (apply gl20/clear-color! args))
