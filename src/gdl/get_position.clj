(ns gdl.get-position
  (:require [gdl.vector3.clojurize :as clojurize])
  (:import (com.badlogic.gdx Input)
           (com.badlogic.gdx.graphics OrthographicCamera)))

(defprotocol P
  (f [_]))

(extend-type Input
  P
  (f [input]
    [(.getX input)
     (.getY input)]))

(extend-type OrthographicCamera
  P
  (f [camera]
    (clojurize/f (.position camera))))
