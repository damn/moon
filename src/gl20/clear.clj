(ns gl20.clear
  (:import (com.badlogic.gdx.graphics GL20)))

(defn f [^GL20 gl bit-mask]
  (.glClear gl bit-mask))
