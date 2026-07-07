(ns clojure.overlaps
  (:require [clojure.rectangle :as rectangle]
            [clojure.body-rectangle :refer [->rectangle]]))

(defn overlaps? [body other-body]
  (rectangle/overlaps? (->rectangle body)
                      (->rectangle other-body)))
