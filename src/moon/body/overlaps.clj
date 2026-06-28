(ns moon.body.overlaps
  (:require [moon.body.rectangle :refer [->rectangle]])
  (:import (com.badlogic.gdx.math Rectangle)))

(defn overlaps? [body other-body]
  (.overlaps ^Rectangle (->rectangle body)
             ^Rectangle (->rectangle other-body)))
