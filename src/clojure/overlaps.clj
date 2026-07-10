(ns clojure.overlaps
  (:require [gdl.math.rectangle :as rectangle]
            [com.badlogic.gdx.math.rectangle :as gdx-rectangle]
            [clojure.body.rectangle :refer [->rectangle]]))

(defn overlaps? [body other-body]
  (gdx-rectangle/overlaps (->rectangle body)
                      (->rectangle other-body)))
