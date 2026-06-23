(ns orthographic-camera.position
  (:require [gdx.vector3.clojurize :as clojurize])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [camera]
  (clojurize/f (.position camera)))
