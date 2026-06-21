(ns com.badlogic.gdx.graphics.orthographic-camera.get-position
  (:require [com.badlogic.gdx.math.vector3.clojurize :as clojurize])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn get-position [^OrthographicCamera camera]
  (clojurize/f (.position camera)))
