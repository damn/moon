(ns orthographic-camera.position
  (:require [gdl.vector3.clojurize :as clojurize])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [camera]
  (clojurize/f (.position camera)))
