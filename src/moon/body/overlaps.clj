(ns moon.body.overlaps
  (:require [com.badlogic.gdx.math.rectangle :as rectangle]
            [moon.body.rectangle :refer [->rectangle]]))

(defn overlaps? [body other-body]
  (rectangle/overlaps (->rectangle body)
                      (->rectangle other-body)))
