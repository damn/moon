(ns gl20.clear
  (:import (com.badlogic.gdx.graphics GL20)))

(defn f [gl bit-mask]
  (.glClear ^GL20 gl bit-mask))
