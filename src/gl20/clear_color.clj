(ns gl20.clear-color
  (:require [com.badlogic.gdx.graphics.gl20 :as gl20]))

(defn f [gl r g b a]
  (gl20/clear-color gl r g b a))
