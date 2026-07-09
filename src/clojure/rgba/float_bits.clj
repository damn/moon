(ns clojure.rgba.float-bits
  (:require [com.badlogic.gdx.graphics.color :as color]))

(defn f [& args]
  (apply color/to-float-bits args))
