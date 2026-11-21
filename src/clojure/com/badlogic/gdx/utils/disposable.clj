(ns clojure.com.badlogic.gdx.utils.disposable
  (:import (com.badlogic.gdx.utils Disposable)))

(defn dispose! [^Disposable disposable]
  (.dispose disposable))
