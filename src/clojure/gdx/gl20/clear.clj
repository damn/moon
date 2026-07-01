(ns clojure.gdx.gl20.clear
  (:import (com.badlogic.gdx.graphics GL20)))

(defn f [gl bit-mask]
  (GL20/.glClear gl bit-mask))
