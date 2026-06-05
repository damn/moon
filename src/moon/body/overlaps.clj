(ns moon.body.overlaps
  (:require [moon.body.rectangle :refer [->rectangle]]
            [clojure.math.rectangle.overlaps :as rectangle]))

(defn overlaps? [body other-body]
  (rectangle/overlaps? (->rectangle body)
                       (->rectangle other-body)))
