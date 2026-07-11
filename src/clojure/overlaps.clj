(ns clojure.overlaps
  (:require [com.badlogic.gdx.math.rectangle :as gdx-rectangle]
            [moon.body :as body]))

(defn overlaps? [body other-body]
  (gdx-rectangle/overlaps (body/rectangle body)
                          (body/rectangle other-body)))
