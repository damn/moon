(ns clojure.com.badlogic.gdx.utils.disposable
  "Interface for disposable resources."
  (:import (com.badlogic.gdx.utils Disposable)))

(defn dispose!
  "Releases all resources of this object."
  [^Disposable disposable]
  (.dispose disposable))
