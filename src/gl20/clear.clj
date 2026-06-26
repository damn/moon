(ns gl20.clear
  (:require [com.badlogic.gdx.graphics.gl20 :as gl20]))

(defn f [gl bit-mask]
  (gl20/clear gl bit-mask))
