(ns moon.body.overlaps
  (:require [clojure.gdx :as gdx]
            [moon.body.rectangle :refer [->rectangle]]))

(defn overlaps? [body other-body]
  (gdx/rectangle-overlaps? (->rectangle body)
                           (->rectangle other-body)))
