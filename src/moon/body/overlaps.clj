(ns moon.body.overlaps
  (:require [clojure.gdx.rectangle.overlaps :as overlaps]
            [moon.body.rectangle :refer [->rectangle]]))

(defn overlaps? [body other-body]
  (overlaps/f (->rectangle body)
              (->rectangle other-body)))
