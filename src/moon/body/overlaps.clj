(ns moon.body.overlaps
  (:require [moon.body.rectangle :refer [->rectangle]]
            [clojure.gdx.math.rectangle :as gdx-rectangle]))

(defn overlaps? [body other-body]
  (gdx-rectangle/overlaps? (->rectangle body)
                           (->rectangle other-body)))
