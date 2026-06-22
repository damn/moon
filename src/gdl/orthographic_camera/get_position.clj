(ns gdl.orthographic-camera.get-position
  (:require [clojure.math.vector3.clojurize :as clojurize])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn get-position [^OrthographicCamera camera]
  (clojurize/f (.position camera)))
