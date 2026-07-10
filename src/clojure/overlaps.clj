(ns clojure.overlaps
  (:require [gdl.math.rectangle :as rectangle]
            [clojure.body.rectangle :refer [->rectangle]]))

(defn overlaps? [body other-body]
  (rectangle/overlaps? (->rectangle body)
                      (->rectangle other-body)))
