(ns com.badlogic.gdx.graphics.delta-time
  (:import (com.badlogic.gdx Graphics)))

(defn f [^Graphics graphics]
  (.getDeltaTime graphics))

