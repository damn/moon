(ns orthographic-camera.position
  (:require [gdx.math.vector3.clojurize :as clojurize])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [^OrthographicCamera camera]
  (clojurize/f (.position camera)))
